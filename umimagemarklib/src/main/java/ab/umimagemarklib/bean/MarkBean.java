package ab.umimagemarklib.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AB051788 on 2017/5/17.
 */
public class MarkBean implements Serializable{
	private String version;
	private String imageID;
	private String imageURL;
	private List<LayerBean> layers;

	public MarkBean() {
	}

	public MarkBean(List<LayerBean> layers) {
		this.layers = layers;
	}

	public MarkBean(String imageURL, List<LayerBean> layers) {
		this.imageURL = imageURL;
		this.layers = layers;
	}


	@Override
	public String toString() {
		return "MarkBean{" +
				"version='" + version + '\'' +
				", imageID='" + imageID + '\'' +
				", imageURL='" + imageURL + '\'' +
				", layers=" + layers +
				'}';
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public List<LayerBean> getLayers() {
		return layers;
	}

	public void setLayers(List<LayerBean> layers) {
		this.layers = layers;
	}


}
