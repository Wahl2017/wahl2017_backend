package at.wahl2017.backend.tools;

public class Text {

	public static String strip(String input) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<input.length();i++) {
			char ch = input.charAt(i);
			if(Character.isWhitespace(ch) || Character.isLetterOrDigit(ch)) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}
	
	public static final String LAST_3_BYTE_UTF_CHAR = "\uFFFF";
	public static final String REPLACEMENT_CHAR = "\uFFFD"; 

	public static String toValid3ByteUTF8String(String s)  {
	    final int length = s.length();
	    StringBuilder b = new StringBuilder(length);
	    for (int offset = 0; offset < length; ) {
	       final int codepoint = s.codePointAt(offset);

	       // do something with the codepoint
	       if (codepoint > Text.LAST_3_BYTE_UTF_CHAR.codePointAt(0)) {
	           b.append(Text.REPLACEMENT_CHAR);
	       } else {
	           if (Character.isValidCodePoint(codepoint)) {
	               b.appendCodePoint(codepoint);
	           } else {
	               b.append(Text.REPLACEMENT_CHAR);
	           }
	       }
	       offset += Character.charCount(codepoint);
	    }
	    return b.toString();
	}
	
}
