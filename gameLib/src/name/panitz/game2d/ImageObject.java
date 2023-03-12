package name.panitz.game2d;
import java.awt.*;
import java.util.Objects;
import javax.swing.ImageIcon;

public final class ImageObject
        implements GameObj {
  private final Vertex pos;
  private final Vertex velocity;
  public int kundenart;
  private double width;
  private double height;
  private String fileName;
  private Image image;


  public ImageObject(Vertex pos, Vertex velocity, double width, double height, String fileName, Image image) {
    var iIcon
            = new ImageIcon(getClass().getClassLoader().getResource(fileName));
    width = iIcon.getIconWidth();
    height = iIcon.getIconHeight();
    image = iIcon.getImage();
    this.pos = pos;
    this.velocity = velocity;
    this.width = width;
    this.height = height;
    this.fileName = fileName;
    this.image = image;
  }

  public ImageObject(Vertex pos, Vertex velocity, String fileName) {
    this(pos, velocity, 0, 0, fileName, null);
  }

  public ImageObject(String fileName) {
    this(new Vertex(0, 0), new Vertex(0, 0), fileName);
  }

  public void paintTo(Graphics g) {
    g.drawImage(image, (int) pos.x, (int) pos.y, null);
    g.setColor(Color.red);
    //g.drawRect((int) this.pos().x, (int) this.pos().y, (int) this.width, (int) this.height);
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

  public String fileName() {
    return fileName;
  }

  public void setImage(String fileName) {
    var iIcon = new ImageIcon(getClass().getClassLoader().getResource(fileName));
    width = iIcon.getIconWidth();
    height = iIcon.getIconHeight();
    image = iIcon.getImage();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (ImageObject) obj;
    return Objects.equals(this.pos, that.pos) &&
            Objects.equals(this.velocity, that.velocity) &&
            Double.doubleToLongBits(this.width) == Double.doubleToLongBits(that.width) &&
            Double.doubleToLongBits(this.height) == Double.doubleToLongBits(that.height) &&
            Objects.equals(this.fileName, that.fileName) &&
            Objects.equals(this.image, that.image);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, velocity, width, height, fileName, image);
  }

  @Override
  public String toString() {
    return "ImageObject[" +
            "pos=" + pos + ", " +
            "velocity=" + velocity + ", " +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "fileName=" + fileName + ", " +
            "image=" + image + ']';
  }

}

