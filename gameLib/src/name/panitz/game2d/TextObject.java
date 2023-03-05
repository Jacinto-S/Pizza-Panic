package name.panitz.game2d;
import java.awt.*;
import java.util.Objects;

public class TextObject
        implements GameObj {
  private final Vertex pos;
  private final Vertex velocity;
  private final double width;
  private final double height;
  private final int fontSize;
  private final String fontName;
  public String text;

  public TextObject(Vertex pos, Vertex velocity
          , double width, double height
          , int fontSize, String fontName, String text) {
    this.pos = pos;
    this.velocity = velocity;
    this.width = width;
    this.height = height;
    this.fontSize = fontSize;
    this.fontName = fontName;
    this.text = text;
  }

  public TextObject(Vertex pos, String text) {
    this(pos, new Vertex(0, 0), 0, 0, 20, "Helvetica", text);
  }


  public void paintTo(Graphics g) {
    g.setFont(new Font(fontName, Font.PLAIN, fontSize));
    g.drawString(text, (int) pos().x, (int) pos().y);
  }

  @Override
  public Vertex pos() {
    return pos;
  }

  @Override
  public Vertex velocity() {
    return velocity;
  }

  @Override
  public double width() {
    return width;
  }

  @Override
  public double height() {
    return height;
  }

  public int fontSize() {
    return fontSize;
  }

  public String fontName() {
    return fontName;
  }

  public String text() {
    return text;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TextObject) obj;
    return Objects.equals(this.pos, that.pos) &&
            Objects.equals(this.velocity, that.velocity) &&
            Double.doubleToLongBits(this.width) == Double.doubleToLongBits(that.width) &&
            Double.doubleToLongBits(this.height) == Double.doubleToLongBits(that.height) &&
            this.fontSize == that.fontSize &&
            Objects.equals(this.fontName, that.fontName) &&
            Objects.equals(this.text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, velocity, width, height, fontSize, fontName, text);
  }

  @Override
  public String toString() {
    return "TextObject[" +
            "pos=" + pos + ", " +
            "velocity=" + velocity + ", " +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "fontSize=" + fontSize + ", " +
            "fontName=" + fontName + ", " +
            "text=" + text + ']';
  }

}

