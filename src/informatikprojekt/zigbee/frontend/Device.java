package informatikprojekt.zigbee.frontend;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Device {

    private static final Image src = new Image("informatikprojekt/zigbee/frontend/cursor/sensor2.png");

    private int id;
    private Text text;
    private ImageView imageView;

    public Device(int id, double x, double y) {
        this.id = id;

        imageView = new ImageView(src);
        imageView.setFitHeight(40);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setPreserveRatio(true);

        text = new Text("ID: " + (id+1));
        text.setX(x);
        text.setY(y - 10);

    }

    public ImageView getImageView() {
        return imageView;
    }

    public Text getText() {
        return text;
    }

    public int getID(){
        return id;
    }

}
