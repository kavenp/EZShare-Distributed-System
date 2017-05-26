package assist;
import java.util.Random;

/* Class for creating random alphanumeric strings used for generating secret 
 * note: this is insecure because of how random seed works but cheap in resources */
public class RandomString {

  private static final char[] symbols;

  static {
    StringBuilder tmp = new StringBuilder();
    for (char ch = '0'; ch <= '9'; ++ch)
      tmp.append(ch);
    for (char ch = 'a'; ch <= 'z'; ++ch)
      tmp.append(ch);
    symbols = tmp.toString().toCharArray();
  }   

  private final Random random = new Random();

  private final char[] buf;

  public RandomString(int length) {
    if (length < 1)
      throw new IllegalArgumentException("length < 1: " + length);
    buf = new char[length];
  }

  public String genString() {
    for (int i = 0; i < buf.length; ++i) 
      buf[i] = symbols[random.nextInt(symbols.length)];
    return new String(buf);
  }
}