package extras;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieHandler {
	public static Cookie getCookieWithName(HttpServletRequest request, String name) {
		for(Cookie cookie : request.getCookies()) {
			if(name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}
}
