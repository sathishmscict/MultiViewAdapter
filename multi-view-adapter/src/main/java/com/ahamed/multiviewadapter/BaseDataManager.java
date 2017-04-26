package com.ahamed.multiviewadapter;

import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.List;

class BaseDataManager<M> implements ListUpdateCallback {

  private final RecyclerListAdapter listAdapter;
  private List<M> dataList = new ArrayList<>();
  private SparseBooleanArray selectedItems = new SparseBooleanArray();

  BaseDataManager(RecyclerListAdapter baseAdapter) {
    this.listAdapter = baseAdapter;
  }

  @Override public final void onInserted(int position, int count) {
    listAdapter.notifyBinderItemRangeInserted(this, position, count);
  }

  @Override public final void onRemoved(int position, int count) {
    listAdapter.notifyBinderItemRangeRemoved(this, position, count);
  }

  @Override public final void onMoved(int fromPosition, int toPosition) {
    listAdapter.notifyBinderItemMoved(this, fromPosition, toPosition);
  }

  @Override public final void onChanged(int position, int count, Object payload) {
    listAdapter.notifyBinderItemRangeChanged(this, position, count, payload);
  }

  /**
   * Called by the {@link BaseDataManager} when it wants to check whether two items have the same
   * data.
   * BaseDataManager uses this information to detect if the contents of an item has changed.
   * <p>
   * BaseDataManager uses this method to check equality instead of {@link Object#equals(Object)}
   * so that you can change its behavior depending on your UI.
   *
   * @param oldItem The item in the old list
   * @param newItem The item in the new list which replaces the oldItem
   * @return True if the contents of the items are the same or false if they are different, ie., you
   * should return whether the items' visual representations are the same.
   */
  public boolean areContentsTheSame(M oldItem, M newItem) {
    return oldItem.equals(newItem);
  }

  @SuppressWarnings("UnusedParameters") public Object getChangePayload(M oldItem, M newItem) {
    return null;
  }

  public List<M> getSelectedItems() {
    List<M> selectedItemsList = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      if (selectedItems.get(i)) {
        selectedItemsList.add(dataList.get(i));
      }
    }
    return selectedItemsList;
  }

  public void setSelectedItems(List<M> selectedItems) {
    if (!(listAdapter instanceof SelectableAdapter)) {
      throw new IllegalStateException(
          "Make sure your adapter extends from com.ahamed.multiviewadapter.SelectableAdapter");
    }
    if (size() < 0) {
      return;
    }
    SparseBooleanArray oldSelectedItems = this.selectedItems.clone();
    this.selectedItems = new SparseBooleanArray();
    int i = 0;
    for (M m : selectedItems) {
      boolean isSelected = contains(m);
      this.selectedItems.put(i, isSelected);
      if (oldSelectedItems.get(i, false) != isSelected) {
        onItemSelectionToggled(i, isSelected);
      }
      i++;
    }
  }

  @Nullable public M getSelectedItem() {
    for (int i = 0; i < size(); i++) {
      if (selectedItems.get(i)) {
        return dataList.get(i);
      }
    }
    return null;
  }

  public void setSelectedItem(M selectedItem) {
    if (!(listAdapter instanceof SelectableAdapter)) {
      throw new IllegalStateException(
          "Make sure your adapter extends from com.ahamed.multiviewadapter.SelectableAdapter");
    }
    if (size() < 0) {
      return;
    }
    M previousSelectedItem = getSelectedItem();
    int index = indexOf(selectedItem);
    if (index != -1) {
      this.selectedItems.put(index, true);
      onItemSelectionToggled(index, true);
      ((SelectableAdapter) listAdapter).setLastSelectedIndex(index);
    }
    if (null != previousSelectedItem && indexOf(previousSelectedItem) != -1) {
      onItemSelectionToggled(indexOf(previousSelectedItem), false);
    }
  }

  public final int getCount() {
    return size();
  }

  public final boolean isEmpty() {
    return dataList.isEmpty();
  }

  public final boolean contains(M item) {
    return dataList.contains(item);
  }

  public final M get(int index) {
    return dataList.get(index);
  }

  public final int indexOf(M item) {
    return dataList.indexOf(item);
  }

  public final int lastIndexOf(M item) {
    return dataList.lastIndexOf(item);
  }

  ///////////////////////////////////////////
  /////////// Internal API ahead. ///////////
  ///////////////////////////////////////////

  void onItemSelectionToggled(int position, boolean isSelected) {
    selectedItems.put(position, isSelected);
    onChanged(position, 1, null);
  }

  List<M> getDataList() {
    return dataList;
  }

  void setDataList(List<M> dataList) {
    this.dataList = new ArrayList<>(dataList);
  }

  int size() {
    return dataList.size();
  }

  M getItem(int dataItemPosition) {
    return dataList.get(dataItemPosition);
  }

  boolean isItemSelected(int dataItemPosition) {
    return selectedItems.get(dataItemPosition);
  }
}