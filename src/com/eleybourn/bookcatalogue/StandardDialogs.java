/*
 * @copyright 2011 Philip Warner
 * @license GNU General Public License
 * 
 * This file is part of Book Catalogue.
 *
 * Book Catalogue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Book Catalogue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Book Catalogue.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.eleybourn.bookcatalogue;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;

public class StandardDialogs {

	public static void deleteSeriesAlert(Context context, final CatalogueDBAdapter dbHelper, final Series series, final Runnable onDeleted) {

		// When we get here, we know the names are genuinely different and the old series is used in more than one place.
		String message = String.format(context.getResources().getString(R.string.really_delete_series), series.name);
		final AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).create();

		alertDialog.setTitle(R.string.delete_series);
		alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteSeries(series);
				alertDialog.dismiss();
				onDeleted.run();
			}
		});

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		}); 

		alertDialog.show();
	}

	public static int deleteBookAlert(Context context, final CatalogueDBAdapter dbHelper, final long id, final Runnable onDeleted) {

		ArrayList<Author> authorList = dbHelper.getBookAuthorList(id);

		String title;
		Cursor cur = dbHelper.fetchBookById(id);
		try {
			if (cur == null || !cur.moveToFirst())
				return R.string.unable_to_find_book;

			title = cur.getString(cur.getColumnIndex(CatalogueDBAdapter.KEY_TITLE));
			if (title == null || title.length() == 0)
				title = "<Unknown>";
			
		} finally {
			if (cur != null)
				cur.close();
		}

		// Format the list of authors nicely
		String authors;
		if (authorList.size() == 0)
			authors = "<Unknown>";
		else {
			authors = authorList.get(0).getDisplayName();
			for (int i = 1; i < authorList.size() - 1; i++)
				authors += ", " + authorList.get(i).getDisplayName();
			if (authorList.size() > 2)
				authors += " " + context.getResources().getString(R.string.list_and) + " " + authorList.get(authorList.size()).getDisplayName();
		}

		// Get the title		
		String format = context.getResources().getString(R.string.really_delete_book);
		
		String message = String.format(format, title, authors);
		final AlertDialog alertDialog = new AlertDialog.Builder(context).setMessage(message).create();

		alertDialog.setTitle(R.string.menu_delete);
		alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteBook(id);
				alertDialog.dismiss();
				onDeleted.run();
			}
		});

		alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		}); 

		alertDialog.show();
		return 0;
		
	}

}