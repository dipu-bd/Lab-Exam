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

import com.sun.jna.Native;
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
import com.sun.jna.win32.W32APIOptions;
import java.awt.event.KeyEvent;

/**
 * Sample implementation of a low-level keyboard hook on W32.
 */
public final class KeyHook
{

    private static HHOOK hhk;
    private static volatile boolean quit;
    private static LowLevelKeyboardProc keyboardHook;

    public static void unblockWindowsKey()
    {
        quit = true;
    }

    public static void blockWindowsKey()
    {
        quit = false;
        new Thread()
        {
            public void run()
            {
                final User32 lib = User32.INSTANCE; 
                HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
                keyboardHook = new LowLevelKeyboardProc()
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
                        return lib.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
                    }
                };
                hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);

                // This bit never returns from GetMessage
                int result;
                MSG msg = new MSG();
                while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
                    if (result == -1 || quit) {
                        break;
                    }
                    else {
                        System.err.println("got message");
                        lib.TranslateMessage(msg);
                        lib.DispatchMessage(msg);
                    }
                }
                lib.UnhookWindowsHookEx(hhk);
            }
        }.start();
    }

}
