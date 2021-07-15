package dev.maiky.minetopia.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Numbers {

	public static String convert(Type type, double d) {
		if (type == Type.MONEY) {
			DecimalFormat f = ((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("NL", "nl")));
			f.applyPattern("¤#,##0.00;¤ -#");
			String re = f.format(d);
			return re.endsWith(",00") ? re.substring(0, re.length() - 3) : re;
		} else {
			return new DecimalFormat("0.0").format(d);
		}
		Events.
	}

	public enum Type {
		MONEY,SHARDS;
	}

}
