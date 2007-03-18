/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */

package application;

import java.util.List;


/**
 * Listener used for observing {@code Task} execution.  The three methods here correspond to the same {@code
 * javax.swing.SwingWorker} methods inherited by the Task class.  A {@code TaskListener} is particularly useful for
 * monitoring the the intermediate results {@link Task#publish published} by a Task in situations where it's not
 * practical to override the Task's {@link Task#process process} method.
 * <p/>
 * The Task class runs all TaskListener methods on the event dispatching thread.  Similarly source all TaskEvents is the
 * Task object.
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see Task#addTaskListener
 * @see Task#removeTaskListener
 */
public interface TaskListener<T, V> {

    /**
     * Called just before the Task's {@code doInBackground} method is called, i.e. just before the task begins running.
     * @param event the source of this event is the {@code Task} object
     * @see Task#doInBackground
     */
    void doInBackground(TaskEvent<Void> event);

    /**
     * Called each time the Task's {@code process} method is called. The {@link TaskEvent#getValue value} of the event
     * is the list of values passed to the process method.
     * @param event the list of the values passed to the {@code process} method
     * @see Task#doInBackground
     */
    void process(TaskEvent<List<V>> event);

    /**
     * Called after the Tasks done() method is called.  The event's value is the value returned by the Task's {@link
     * Task#get get} method, i.e. the value that's computed by the Task's {@link Task#doInBackground doInBackground}
     * method.
     * @param event the value produced by the {@code doInBackground} method
     * @see Task#doInBackground
     * @see Task#get
     */
    void done(TaskEvent<T> event);
}
