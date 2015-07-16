/* Copyright (c) 2007 Timothy Wall, All Rights Reserved

 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
package ClientApplication;
 
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;
import com.sun.jna.platform.win32.WinUser.MSG; 

/**
 * Sample implementation of a low-level keyboard hook on W32.
 * For blocking key pass to outside the bound of this application.
 */
public class KeyHook
{
    //windows hook handler
    private HHOOK mWinHook;
    //the value used across threads to notify it to stop
    private volatile boolean mStopBlocking;
    //low level keyboard hook
    private LowLevelKeyboardProc mKeyboardHook;

    /**
     * Start blocking key pass to windows.
     */
    public void unblockWindowsKey()
    {
        mStopBlocking = true;
    }

    /**
     * Stop blocking key pass to windows.
     */
    public void blockWindowsKey()
    {
        mStopBlocking = false;
        new Thread()
        {
            @Override
            public void run()
            {
                final User32 lib = User32.INSTANCE; 
                HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
                mKeyboardHook = new LowLevelKeyboardProc()
                {
                    @Override
                    public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info)
                    {
                        if (nCode >= 0) {
                            /* ref: https://msdn.microsoft.com/en-us/library/windows/desktop/dd375731(v=vs.85).aspx */
                            switch (info.vkCode) {
                                case 0x5B: //left win
                                case 0x5C: //right win
                                case 0x5D: //application 
                                case 0xA4: //left menu 
                                case 0xA5: //right menu 
                                case 0x1B: //escape key
                                    return new LRESULT(1);
                            }
                        } 
                        return lib.CallNextHookEx(mWinHook, nCode, wParam, info.getPointer());
                    }
                };
                mWinHook = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, mKeyboardHook, hMod, 0);

                // This bit never returns from GetMessage
                int result;
                MSG msg = new MSG();
                while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                    if (result == -1 || mStopBlocking) {
                        break;
                    }
                    else {
                        System.err.println("got message");
                        lib.TranslateMessage(msg);
                        lib.DispatchMessage(msg);
                    }
                }
                lib.UnhookWindowsHookEx(mWinHook);
            }
        }.start();
    }

}
