package moe.seiua.skymatrix.task

import cn.seiua.skymatrix.client.component.Component
import cn.seiua.skymatrix.client.component.Config
import cn.seiua.skymatrix.client.component.Event
import cn.seiua.skymatrix.client.component.Init
import cn.seiua.skymatrix.event.EventTarget
import cn.seiua.skymatrix.event.events.ClientTickEvent
import com.google.common.collect.EvictingQueue
import java.util.*

@Event(register = true)
@Component
@Config(name = "ModuleManager")
class TaskManager {
    private val priorityQueue: Queue<Runnable> = EvictingQueue.create(40)

    @Init
    fun init() {
        TaskManager.instance = this
    }

    @EventTarget
    fun onTick(event: ClientTickEvent) {
        val runnable = priorityQueue.poll()
        runnable?.run()
    }

    fun postTask(runnable: Runnable) {
        priorityQueue.add(runnable)
    }

    fun postTask(runnable: () -> Unit) {
        postTask(Runnable { runnable() })
    }

    fun postTask(runnable: () -> Unit, delay: Long) {
        postTask(Runnable {
            Thread.sleep(delay)
            runnable()
        })
    }

    companion object {
        lateinit var instance: TaskManager
        fun postTask(runnable: Runnable) {
            instance.postTask(runnable)
        }
    }


}