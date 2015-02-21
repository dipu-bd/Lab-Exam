/*
 * Copyright (C) 2015 Dipu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ExtraClass;

import javax.print.DocPrintJob;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

/**
 *
 * @author Dipu
 */
class PrintJobWatcher {

    // true iff it is safe to close the print job's input stream
    boolean done = false;

    PrintJobWatcher(DocPrintJob job)
    {
        // Add a listener to the print job
        job.addPrintJobListener(new PrintJobAdapter() {
            @Override
            public void printJobCanceled(PrintJobEvent pje)
            {
                allDone();
            }

            @Override
            public void printJobCompleted(PrintJobEvent pje)
            {
                allDone();
            }

            @Override
            public void printJobFailed(PrintJobEvent pje)
            {
                allDone();
            }

            @Override
            public void printJobNoMoreEvents(PrintJobEvent pje)
            {
                allDone();
            }

            void allDone()
            {
                synchronized (PrintJobWatcher.this)
                {
                    done = true;
                    PrintJobWatcher.this.notify();
                }
            }
        });
    }

    public synchronized void waitForDone()
    {
        try
        {
            while (!done)
            {
                wait();
            }
        }
        catch (InterruptedException e)
        {
        }
    }
}
