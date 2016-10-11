package com.yanzi.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

/**
 * Persistently store values on the phone using tags to store and retrieve.
 * 
 */

public class TinyDB {

	/**
	 * Prevent instantiation.
	 */
	private TinyDB() {
	}

	public static void StoreValue(final Context context, final String tag,
			final Object valueToStore) {
		try {
			FileOutputStream fos = context.openFileOutput(tag,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(valueToStore);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public static Object GetValue(final Context context, final String tag) {
		Object value = new Object();
		try {
			FileInputStream filestream = context.openFileInput(tag);
			ObjectInputStream ois = new ObjectInputStream(filestream);
			value = ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (value instanceof Integer) {
			return Integer.parseInt(value.toString());
		} else if (value instanceof Boolean) {
			return Boolean.parseBoolean(value.toString());
		} else if (value instanceof Double) {
			return Double.parseDouble(value.toString());
		} else if (value instanceof Long) {
			return Long.parseLong(value.toString());
		} else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else if (value instanceof String) {

			return value.toString();

		} else if (value instanceof ArrayList<?>) {
			return (ArrayList<?>) value;
		}

		return value;
	}

}
