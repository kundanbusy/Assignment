package api.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import api.utilities.DataSetEnum;

public class DataSets {

	public static Object[][] addDataSetToTest(DataSetEnum e, String fileName) {
		Object[][] arr=null;
		switch (e) {
		case COMMASEPTEXTFILE:
			arr= readFromTextFile(fileName);
			break;
		default:
			break;
		}
		return arr;
	}

	private static Object[][] readFromTextFile(String fileName) {
		String str;
		Object[][] objarr = null;
		String[] arr;
		List<String> datalist = new ArrayList<>();
		int cols, rows;
		BufferedReader rd = null;
		try {
			File file = new File("./Data/Datasets/" + fileName);
			rd = new BufferedReader(new FileReader(file));
			// for reading headers.
			cols = rd.readLine().split(",").length;
			while ((str = rd.readLine()) != null)
				datalist.add(str);
			rd.close();
			rows = datalist.size();
			objarr = new Object[rows][cols];
			int k = 0;
			for (String s : datalist) {
				arr = s.split(",");
				for (int i = 0; i < cols; i++)
					objarr[k][i] = arr[i];
				k++;
			}
			return objarr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
