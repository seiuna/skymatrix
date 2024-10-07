package moe.seiua.skymatrix.loader

import kotlinx.coroutines.*
import moe.seiua.skymatrix.task.TaskManager
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
fun setupInjection() {

}

val mainDispatcher = MCoroutineDispatcher()

class MCoroutineDispatcher() : MainCoroutineDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = this


    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return true
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        TaskManager.postTask(block);
    }


    private fun cancelOnRejection(context: CoroutineContext, block: Runnable) {
        context.cancel(CancellationException("The task was rejected, the handler underlying the dispatcher '${toString()}' was closed"))
        Dispatchers.IO.dispatch(context, block)
    }
}