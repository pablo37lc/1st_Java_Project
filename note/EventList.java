package note;

import javafx.beans.property.StringProperty;

public class EventList {
	private StringProperty title;
	private StringProperty note;
	private StringProperty date;
	private StringProperty image;
	
	public EventList(StringProperty title, StringProperty note, StringProperty date, StringProperty image) {
		this.title = title;
		this.note = note;
		this.date = date;
		this.image = image;
	}

	public StringProperty titleProperty() {
		return title;
	}
	
	public String getTitle() {
		return title.get();
	}

	public StringProperty noteProperty() {
		return note;
	}

	public StringProperty dateProperty() {
		return date;
	}

	public String getImage() {
		return image.get();
	}
}
