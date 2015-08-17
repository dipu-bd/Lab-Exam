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
package ClientApplication;

import java.io.File;

/**
 *
 * @author Dipu
 */
public class TreeNodeData extends Object
{
    //file path
    private File mFile;
    //name of file
    private String mName;

    /**
     * Initialize a new instance for TreeNodeObject
     *
     * @param f File name used as object
     */
    public TreeNodeData(File f)
    {
        mFile = f;
        mName = f.getName();
    }

    /**
     * Initialize a new instance for TreeNodeObject
     *
     * @param f File name used as object
     * @param label Custom label for the object
     */
    public TreeNodeData(File f, String label)
    {
        mFile = f;
        mName = label;
    }

    /**
     * Overrides the default toString() method to set the custom name
     *
     * @return String representation of the current object.
     */
    @Override
    public String toString()
    {
        return mName;
    }

    /**
     * Gets the mFile assigned in this object.
     *
     * @return File assigned to this object.
     */
    public File getFile()
    {
        return mFile;
    }
    /**
     * Sets the mFile assigned in this object.
     *
     * @param file File assigned to this object.
     */
    public void setFile(File file)
    {
        mFile = file;
        mName = file.getName();
    }
}
