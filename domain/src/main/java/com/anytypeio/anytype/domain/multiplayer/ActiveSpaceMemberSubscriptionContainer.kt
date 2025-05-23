package com.anytypeio.anytype.domain.multiplayer

import com.anytypeio.anytype.core_models.Config
import com.anytypeio.anytype.core_models.DVFilter
import com.anytypeio.anytype.core_models.DVFilterCondition
import com.anytypeio.anytype.core_models.ObjectType
import com.anytypeio.anytype.core_models.ObjectWrapper
import com.anytypeio.anytype.core_models.Relations
import com.anytypeio.anytype.core_models.primitives.SpaceId
import com.anytypeio.anytype.domain.account.AwaitAccountStartManager
import com.anytypeio.anytype.domain.base.AppCoroutineDispatchers
import com.anytypeio.anytype.domain.debugging.Logger
import com.anytypeio.anytype.domain.library.StoreSearchParams
import com.anytypeio.anytype.domain.library.StorelessSubscriptionContainer
import com.anytypeio.anytype.domain.workspace.SpaceManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface ActiveSpaceMemberSubscriptionContainer {

    fun start()
    fun stop()
    fun observe() : Flow<Store>
    fun observe(space: SpaceId) : Flow<Store>
    fun get() : Store
    fun get(space: SpaceId) : Store

    class Default @Inject constructor(
        private val manager: SpaceManager,
        private val container: StorelessSubscriptionContainer,
        private val scope: CoroutineScope,
        private val dispatchers: AppCoroutineDispatchers,
        private val awaitAccountStart: AwaitAccountStartManager,
        private val logger: Logger
    ) : ActiveSpaceMemberSubscriptionContainer {

        private val data = MutableStateFlow<Store>(Store.Empty)
        private val jobs = mutableListOf<Job>()

        init {
            scope.launch {
                awaitAccountStart.state().collect { state ->
                    when(state) {
                        AwaitAccountStartManager.State.Init -> {
                            // Do nothing
                        }
                        AwaitAccountStartManager.State.Started -> {
                            start()
                        }
                        AwaitAccountStartManager.State.Stopped -> {
                            stop()
                        }
                    }
                }
            }
        }

        override fun observe(): Flow<Store> {
            return data
        }

        override fun observe(space: SpaceId): Flow<Store> {
            return data.map { curr ->
                if (curr is Store.Data && curr.config.space == space.id)
                    curr
                else
                    Store.Empty
            }
        }

        override fun get(): Store {
            return data.value
        }

        override fun get(space: SpaceId): Store {
            val curr = data.value
            return if (curr is Store.Data && curr.config.space == space.id)
                curr
            else
                Store.Empty
        }

        override fun start() {
            jobs += scope.launch(dispatchers.io) {
                manager
                    .observe()
                    .flatMapLatest { config ->
                        container.subscribe(
                            StoreSearchParams(
                                space = SpaceId(config.space),
                                subscription = GLOBAL_SUBSCRIPTION,
                                filters = buildList {
                                    add(
                                        DVFilter(
                                            relation = Relations.LAYOUT,
                                            value = ObjectType.Layout.PARTICIPANT.code.toDouble(),
                                            condition = DVFilterCondition.EQUAL
                                        )
                                    )
                                },
                                limit = 0,
                                keys = listOf(
                                    Relations.ID,
                                    Relations.SPACE_ID,
                                    Relations.IDENTITY,
                                    Relations.PARTICIPANT_PERMISSIONS,
                                    Relations.PARTICIPANT_STATUS,
                                    Relations.LAYOUT,
                                    Relations.NAME,
                                    Relations.PLURAL_NAME,
                                    Relations.ICON_IMAGE,
                                    Relations.GLOBAL_NAME
                                )
                            )
                        ).map { objects ->
                            Store.Data(
                                members = objects.map { obj ->
                                    ObjectWrapper.SpaceMember(obj.map)
                                },
                                config = config
                            )
                        }
                    }
                    .catch { error ->
                        logger.logException(
                            e = error,
                            msg = "Failed to subscribe to active-space-members"
                        )
                    }
                    .collect {
                        data.value = it
                    }
            }
        }

        override fun stop() {
            jobs.forEach { it.cancel() }
            scope.launch(dispatchers.io) {
                container.unsubscribe(listOf(DefaultUserPermissionProvider.GLOBAL_SUBSCRIPTION))
            }
        }

        companion object {
            const val GLOBAL_SUBSCRIPTION = "subscription.global.active-space-members"
        }
    }

    sealed class Store {
        data object Empty : Store()
        data class Data(
            val config: Config,
            val members: List<ObjectWrapper.SpaceMember>
        ) : Store()
    }
}