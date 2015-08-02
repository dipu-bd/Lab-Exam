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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Dipu
 */
public class MyTreeRenderer extends DefaultTreeCellRenderer
{

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        File file = ((TreeNodeData) node.getUserObject()).getFile();
        setToolTipText(file.getAbsolutePath());
        Font curFont = tree.getFont();
        if (node.getLevel() == 0) {
            setIcon(QUES_ICON);
            setFont(new Font(curFont.getFamily(), curFont.getStyle(), curFont.getSize() + 2));
        }
        else if (file.isDirectory()) {
            setIcon(FOLDER_ICON);
            setFont(new Font(curFont.getFamily(), curFont.getStyle(), curFont.getSize() + 1));
        }
        else if (file.isFile()) {
            String ext = file.getName().toLowerCase();
            if (ext.endsWith("java")) {
                setIcon(JAVA_ICON);
            }
            else if (ext.endsWith("cpp")) {
                setIcon(CPP_ICON);
            }
            else if (ext.endsWith("c")) {
                setIcon(ANSIC_ICON);
            }
            else {
                setIcon(FILE_ICON);
            }
        }
        setOpenIcon(getIcon());
        setClosedIcon(getIcon());

        return this;
    }

    //icon pack
    protected ImageIcon FOLDER_ICON = new ImageIcon(getClass().getResource("/Resources/folder.png"));
    protected ImageIcon ANSIC_ICON = new ImageIcon(getClass().getResource("/Resources/ansi_c.png"));
    protected ImageIcon CPP_ICON = new ImageIcon(getClass().getResource("/Resources/cpp.png"));
    protected ImageIcon JAVA_ICON = new ImageIcon(getClass().getResource("/Resources/java.png"));
    protected ImageIcon FILE_ICON = new ImageIcon(getClass().getResource("/Resources/file.png"));
    protected ImageIcon QUES_ICON = new ImageIcon(getClass().getResource("/Resources/question.png"));
}
