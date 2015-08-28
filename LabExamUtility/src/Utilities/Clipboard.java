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
package Utilities;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * System clipboard.
 */
public abstract class Clipboard
{

    /**
     * Gets the current system clipboard.
     *
     * @return Current system clipboard.
     */
    private static java.awt.datatransfer.Clipboard getClipboard()
    {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Gets the text data from clipboard.
     *
     * @return String data if exist; null otherwise.
     */
    public static String getText()
    {
        try {
            Object data = getClipboard().getContents(null)
                    .getTransferData(DataFlavor.stringFlavor);
            if (data instanceof String) {
                return (String) data;
            }
            return null;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * Sets a text data to the clipboard
     *
     * @param val String data to set
     */
    public static void setText(String val)
    {
        getClipboard().setContents(new StringSelection(val), null);
    }

    /**
     * Completely clears the current clipboard.
     */
    public static void ClearAll()
    {
        try {
            getClipboard().setContents(new Transferable()
            {
                @Override
                public DataFlavor[] getTransferDataFlavors()
                {
                    return new DataFlavor[0];
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor)
                {
                    return false;
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
                {
                    throw new UnsupportedFlavorException(flavor);
                }
            }, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the list of files from the clipboard.
     *
     * @return List of files if exist; null otherwise.
     */
    public static File[] getFileList()
    {
        try {
            Object data = getClipboard().getContents(null)
                    .getTransferData(DataFlavor.javaFileListFlavor);
            return (File[]) data;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Sets a list of files to the clipboard to copy.
     *
     * @param files List of files.
     */
    public static void setFileList(final ArrayList<File> files)
    {
        try {
            getClipboard().setContents(
                    new Transferable()
                    {
                        @Override
                        public DataFlavor[] getTransferDataFlavors()
                        {
                            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                        }

                        @Override
                        public boolean isDataFlavorSupported(DataFlavor flavor)
                        {
                            return DataFlavor.javaFileListFlavor.equals(flavor);
                        }

                        @Override
                        public Object getTransferData(DataFlavor flavor)
                        throws UnsupportedFlavorException, IOException
                        {
                            return files;
                        }
                    }, null
            );
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
