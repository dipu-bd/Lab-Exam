/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.io.Serializable;

/**
 * Class that stores answer information.
 */
public class AnswerData implements Serializable {
    
    /**
     * Constructs a answer data. 
     * @param file Relative path where the answer file is located.
     * @param data Byte data of contents of the answer file.
     */
    public AnswerData(String file, byte[] data)
    {
        mFilePath = file;
        mData = data;        
    }
     
    //relative file path
    private final String mFilePath;
    //data of file
    private final byte[] mData; 
     
    
    /**
     * Gets the relative path where the answer file is located.
     * @return File path to store answer file.
     */
    public String getRelativeFilePath() { return mFilePath; }
    
    /**
     * Gets the byte data of contents of the answer file.
     * @return Byte data of the answer file.
     */
    public byte[] getFileData() { return mData; }    
}
