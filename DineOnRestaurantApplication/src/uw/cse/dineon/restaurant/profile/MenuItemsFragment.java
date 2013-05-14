package uw.cse.dineon.restaurant.profile;

import java.util.ArrayList;
import java.util.List;

import uw.cse.dineon.library.MenuItem;
import uw.cse.dineon.library.RestaurantInfo;
import uw.cse.dineon.library.util.DineOnConstants;
import uw.cse.dineon.restaurant.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment that presents a editable list of menu items of this restaurant.
 * 
 * @author mhotan
 */
public class MenuItemsFragment extends ListFragment {

	/**
	 * Adapter for restaurant menu Item adapter. Use this to add new Menuitems
	 */
	private RestaurantMenuItemAdapter mAdapter;
	
	private static final String TAG = "MenuItemsFragment";

	/**
	 * Activity listener.
	 */
	private MenuItemListener mListener;
	
	private uw.cse.dineon.library.Menu currentMenu;

	/**
	 * Creates a MenuItemsFragment that is ready to build and view.
	 * 
	 * @param info
	 *            Restaurant that contains a group of menus that each contain
	 *            menu items
	 * @return A MenuItemsFragment that is ready to display all the items of the
	 *         restaurant
	 */
	public static MenuItemsFragment newInstance(RestaurantInfo info) {
		// Prepare a Bundle argument
		// for starting an activity with
		MenuItemsFragment frag = new MenuItemsFragment();
		Bundle args = new Bundle();
		// args.putParcelable(DineOnConstants.KEY_RESTAURANTINFO, info);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		// IF there are arguments
		// then check if there is a restaurant info instance
		// info can be null
		/*
		 * RestaurantInfo info = getArguments() != null ? (RestaurantInfo)
		 * getArguments() .getParcelable(DineOnConstants.KEY_RESTAURANTINFO) :
		 * null;
		 */
		final RestaurantInfo info = mListener.getInfo();

		// If arguments existed and it invluded a Restaurant Info
		// Proceed
		if (isValid(info)) {
			
			// TODO Handle multiple menus
			if(info.getMenuList().size() < 1){
				info.getMenuList().add(new uw.cse.dineon.library.Menu("Default"));
				Log.d(TAG, "No menu exists, created a default menu!");
			}
			currentMenu = info.getMenuList().get(0);
			
			List<MenuItem> menuitems =  currentMenu.getItems();
			mAdapter = new RestaurantMenuItemAdapter(getActivity(), menuitems);
			setListAdapter(mAdapter);
		} else {
			List<String> defList = new ArrayList<String>();
			defList.add("Illegal Restaurant Info State");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, defList);
			setListAdapter(adapter);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Add your menu entries here
		inflater.inflate(R.menu.menu_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.menu_add_menu_item) {
			// Add new blank item
			MenuItem mi = new MenuItem(mAdapter.getCount() + 1, 0.0, "", "");
			currentMenu.addNewItem(mi);
			mAdapter.add(mi);
			mAdapter.notifyDataSetChanged();
			Log.v(TAG, "Adding new menu item");
			return true;
		} else {		
		return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuItemListener) {
			mListener = (MenuItemListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet MenuItemsFragment.MenuItemListener");
		}
	}

	/**
	 * Checks whether the current restaurant info is a valid. restaurant info
	 * instance
	 * 
	 * @param info
	 *            info to be analyzed
	 * @return true if it is valid
	 */
	private boolean isValid(RestaurantInfo info) {
		if (info == null) {
			return false;
		}

		if (DineOnConstants.DEBUG) {
			// TODO Implement
		}
		// TODO Auto-generated method stub
		return true;
	}

	// ////////////////////////////////////////////////////
	// // Following is the interface in which activities
	// // that wish to attach this Fragment must implement
	// // Intended to use for user input
	// ////////////////////////////////////////////////////

	/**
	 * Interface for all owning activities to implement.
	 * 
	 * @author mhotan
	 */
	public interface MenuItemListener {

		/**
		 * Notifies that the user chooses to delete the current menu item.
		 * 
		 * @param item
		 *            menu item to delete
		 */
		void onMenuItemDeleted(MenuItem item);

		/**
		 * Notifies that the user chose to add the current menu item.
		 * 
		 * @param item
		 *            menu item to add
		 */
		void onMenuItemAdded(MenuItem item);

		// XXX Seems like this might be redundant right now

		/**
		 * Notifies that the user chose to modify the current item.
		 * 
		 * @param item
		 *            menu item to modify
		 */
		void onMenuItemModified(MenuItem item);

		RestaurantInfo getInfo();

	}

	/**
	 * Adapter to handle the modification of menu items.
	 * 
	 * @author mhotan
	 */
	private class RestaurantMenuItemAdapter extends ArrayAdapter<MenuItem> {

		/**
		 * Context to use this adapter.
		 */
		private final Context mContext;

		/**
		 * list of items to show.
		 */
		private final List<MenuItem> mItems;

		/**
		 * Creates a menu item adapter for displaying, modifying, and deleting
		 * menu items.
		 * 
		 * @param ctx
		 *            Context to use adapter
		 * @param items
		 *            items to add to this adapter
		 */
		public RestaurantMenuItemAdapter(Context ctx, List<MenuItem> items) {
			super(ctx, R.layout.listitem_menuitem_editable, items);
			mContext = ctx;
			mItems = new ArrayList<MenuItem>(items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position >= this.getCount())
				super.getView(position, convertView, parent);
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view;
			if (convertView != null) {
				view = convertView;
			} else {
				view = inflater.inflate(R.layout.listitem_menuitem_editable, parent, false);
			}

			// Obtain the view used for this menu item
			ImageView image = (ImageView) view.findViewById(R.id.image_thumbnail_menuitem);
			TextView title = (TextView) view.findViewById(R.id.label_menuitem_editable);
			ImageButton delete = (ImageButton) view.findViewById(R.id.button_discard_item);
			EditText description = (EditText) view.findViewById(R.id.edittext_description);
			EditText price = (EditText) view.findViewById(R.id.edittext_price);
			ImageButton save = (ImageButton) view.findViewById(R.id.button_save_menuitem);

			MenuItem item = super.getItem(position);
			title.setText(item.getTitle());
			description.setText(item.getDescription());

			ItemListener listener = new ItemListener(item, image, title, delete, save, description,
					price);

			return view;
		}
	}

	/**
	 * 
	 * @author mhotan
	 * 
	 */
	private class ItemListener implements View.OnClickListener {

		private final MenuItem mItem;
		private final ImageView mImage;
		private final TextView mTitle;
		private final ImageButton mDelete, mSave;
		private final EditText mDescription;
		private final EditText mPrice;

		/**
		 * Implicitly adds listeners.
		 * 
		 * @param item
		 *            MenuItem
		 * @param image
		 *            ImageView
		 * @param title
		 *            TextView
		 * @param delete
		 *            ImageButton
		 * @param save
		 *            ImageButton
		 * @param description
		 *            EditText
		 * @param price
		 */
		public ItemListener(MenuItem item, ImageView image, TextView title, ImageButton delete,
				ImageButton save, EditText description, EditText price) {
			mItem = item;
			mImage = image;
			mTitle = title;
			mDelete = delete;
			mSave = save;
			mDescription = description;
			mPrice = price;

			mImage.setOnClickListener(this);
			mDelete.setOnClickListener(this);
			mSave.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (v == mImage) {
				// TODO Use listener to change the image this menu item
				// Use alert dialog
				// delete mItem

			} else if (v == mDelete) {
				// use alert dialog
				// delete mItem
				mListener.onMenuItemDeleted(mItem);

			} else if (v == mSave) {
				// TODO User listener to save this menu item
				// use alert dialog
				// save mItem
				String newTitle = mTitle.getText().toString().trim();
				String newDesc = mDescription.getText().toString().trim();
				double newPrice = Double.valueOf(mPrice.getText().toString());

				if (newTitle == "" || newDesc == null) {
					Toast.makeText(getActivity(), "Please fill in the Title and Description!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				mItem.setDescription(newDesc);
				mItem.setTitle(newTitle);
				mItem.setPrice(newPrice);
				mListener.onMenuItemModified(mItem);

				// mItem.setDescription(newDescription);
				// TODO Add more modifications here

			}
		}
	}
}
