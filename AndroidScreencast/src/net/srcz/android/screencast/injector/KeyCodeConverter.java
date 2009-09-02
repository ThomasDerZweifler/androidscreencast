package net.srcz.android.screencast.injector;

import java.awt.event.KeyEvent;

public class KeyCodeConverter {

	public static int getKeyCode(KeyEvent e) {
		char c = e.getKeyChar();
		int code = 0;
		if(Character.isLetter(c))
			code = ConstEvtKey.KEYCODE_A + (Character.toLowerCase(c)-'a');
		if(Character.isDigit(c))
			code = ConstEvtKey.KEYCODE_0 + (c-'0');
		
		if(c == '\n')
			code = ConstEvtKey.KEYCODE_ENTER;

		if(c == ' ')
			code = ConstEvtKey.KEYCODE_SPACE; 

		if(c == '\b')
			code = ConstEvtKey.KEYCODE_DEL; 

		if(e.getKeyCode() == KeyEvent.VK_HOME)
			code = ConstEvtKey.KEYCODE_HOME; 

		if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
			code = ConstEvtKey.KEYCODE_MENU; 

		if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
			code = ConstEvtKey.KEYCODE_STAR; 

		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			code = ConstEvtKey.KEYCODE_BACK; 

		if(e.getKeyCode() == KeyEvent.VK_F3)
			code = ConstEvtKey.KEYCODE_CALL; 

		if(e.getKeyCode() == KeyEvent.VK_F4)
			code = ConstEvtKey.KEYCODE_ENDCALL; 

		if(e.getKeyCode() == KeyEvent.VK_F5)
			code = ConstEvtKey.KEYCODE_SEARCH; 

		if(e.getKeyCode() == KeyEvent.VK_F7)
			code = ConstEvtKey.KEYCODE_POWER; 

		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			code = ConstEvtKey.KEYCODE_DPAD_RIGHT; 

		if(e.getKeyCode() == KeyEvent.VK_UP)
			code = ConstEvtKey.KEYCODE_DPAD_UP; 

		if(e.getKeyCode() == KeyEvent.VK_DOWN)
			code = ConstEvtKey.KEYCODE_DPAD_DOWN; 

		if(e.isShiftDown())
			code |= ConstEvtKey.KEYCODE_SHIFT_LEFT;
		
		if(e.isAltDown())
			code |= ConstEvtKey.KEYCODE_ALT_LEFT;
		return code;
	}
	
}
