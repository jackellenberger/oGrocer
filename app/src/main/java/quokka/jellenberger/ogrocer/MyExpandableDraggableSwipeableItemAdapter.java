/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package quokka.jellenberger.ogrocer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

class MyExpandableDraggableSwipeableItemAdapter
        extends AbstractExpandableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.MyGroupViewHolder, MyExpandableDraggableSwipeableItemAdapter.MyChildViewHolder>
        implements ExpandableDraggableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.MyGroupViewHolder, MyExpandableDraggableSwipeableItemAdapter.MyChildViewHolder>,
        ExpandableSwipeableItemAdapter<MyExpandableDraggableSwipeableItemAdapter.MyGroupViewHolder, MyExpandableDraggableSwipeableItemAdapter.MyChildViewHolder> {
    private static final String TAG = "MyEDSItemAdapter";

    private static final int[] EMPTY_STATE = new int[] {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {}

    private interface Draggable extends DraggableItemConstants {}

    private interface Swipeable extends SwipeableItemConstants {}

    private final RecyclerViewExpandableItemManager mExpandableItemManager;
    private AbstractExpandableDataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public interface EventListener {
        void onGroupItemRemoved(int groupPosition);

        void onChildItemRemoved(int groupPosition, int childPosition);

        void onGroupItemPinned(int groupPosition);

        void onChildItemPinned(int groupPosition, int childPosition);

        void onItemViewClicked(View v, boolean pinned);
    }

    public static abstract class MyBaseViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle, mCheckBox, mDeleteItem;
        public TextView mTextView;
        private int mExpandStateFlags;
        public int tabID;

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mCheckBox = v.findViewById(R.id.cart_checkbox);
            mDeleteItem = v.findViewById(R.id.cart_recycler_delete);
            mTextView = (TextView) v.findViewById(R.id.recycler_item_text);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }
        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }
        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public static class MyGroupViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle, mCheckBox, mDeleteItem;
        public TextView mTextView;
        private int mExpandStateFlags;
        public int tabID;
        Context holderContext;

        public MyGroupViewHolder(View v, int tabID){
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mCheckBox = v.findViewById(R.id.cart_checkbox);
            mDeleteItem = v.findViewById(R.id.cart_recycler_delete);
            mTextView = (TextView) v.findViewById(R.id.recycler_item_text);
            this.tabID = tabID;
            this.holderContext = v.getContext();
        }
        @Override
        public int getExpandStateFlags() { return mExpandStateFlags; }
        @Override
        public void setExpandStateFlags(int flag) { mExpandStateFlags = flag; }
        @Override
        public View getSwipeableContainerView() { return mContainer; }
    }

    public static class MyChildViewHolder extends AbstractDraggableSwipeableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        private int mExpandStateFlags;
        public int tabID;
        private int mCurrentGroup;

        public MyChildViewHolder(View v, int tabID) {
            super(v);
            this.tabID = tabID;
            mContainer = (FrameLayout) v.findViewById(R.id.container);
        }
        @Override
        public int getExpandStateFlags() { return mExpandStateFlags; }
        @Override
        public void setExpandStateFlags(int flag) { mExpandStateFlags = flag; }
        @Override
        public View getSwipeableContainerView() { return mContainer; }

        public void setCurrentGroup(int group) {mCurrentGroup = group;}
        public int getCurrentGroup() {return mCurrentGroup;}
    }

    public MyExpandableDraggableSwipeableItemAdapter(
            RecyclerViewExpandableItemManager expandableItemManager,
            AbstractExpandableDataProvider dataProvider) {
        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ItemViewOnClickListener",v.getClass().getName());
                    onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //child item click
                onSwipeableViewContainerClick(v);
            }
        };

        // ExpandableItemAdapter, ExpandableDraggableItemAdapter and ExpandableSwipeableItemAdapter
        // require stable ID, and also have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
        Log.d("onItemViewClick", v.getClass().getName());
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(v, true);  // true --- pinned
        }
    }

    private void onSwipeableViewContainerClick(View v) {
        Log.d("onSwipeableViewContainerClick", "child click disabled");
        return;
        /*
        if (mEventListener != null) {
            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
        }
        */

    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v;
        if (parent.findViewById(R.id.shopping_cart_recycler) != null) {
            v = inflater.inflate(R.layout.shopping_cart_recycler_item, parent, false);
            CheckBox cb = (CheckBox) v.findViewById(R.id.cart_checkbox);
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShoppingCartTabContent.toggleCartItemChecked(v);
                }
            });
            return new MyGroupViewHolder(v,0);
        }
        else {
            v = inflater.inflate(R.layout.saved_cart_recycler_item, parent, false);
            ImageView iv = (ImageView) v.findViewById(R.id.cart_checkbox);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SavedCartTabContent.movedSavedItemToCart(v);
                }
            });
            return new MyGroupViewHolder(v, 1);
        }

    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v;
        if (parent.findViewById(R.id.shopping_cart_recycler) != null) {
            v = inflater.inflate(R.layout.shopping_cart_expanded_item_container, parent, false);
            return new MyChildViewHolder(v,0);
        }
        else {
            v = inflater.inflate(R.layout.saved_cart_recycler_item, parent, false);
            return new MyChildViewHolder(v,1);
        }
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        // group item
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        // set listeners
        holder.itemView.setOnClickListener(mItemViewOnClickListener);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int expandState = holder.getExpandStateFlags();
        final int swipeState = holder.getSwipeStateFlags();
        /*
        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) ||
                ((expandState & Expandable.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;
            boolean isExpanded;
            //boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_group_item_swiping_active_state;
            } else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_group_item_swiping_state;
            } else if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            //holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
        */
        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);

    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        holder.setCurrentGroup(groupPosition);
        // child item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);
        /*
        // set listeners
        // (if the item is *not pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        // (if the item is *pinned*, click event comes to the mContainer)
        holder.mContainer.setOnClickListener(mSwipeableViewContainerOnClickListener);

        // set text
        holder.mTextView.setText(item.getText());
        */
        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0) || ((swipeState & Swipeable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0)
            {
                bgResId = R.drawable.bg_item_dragging_active_state;
                // need to clear drawable state here to get correct appearance of the dragging item.
                clearState(holder.mContainer.getForeground());
            }
            else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0)
            {
                bgResId = R.drawable.bg_item_dragging_state;
            }
            else if ((swipeState & Swipeable.STATE_FLAG_IS_ACTIVE) != 0)
            {
                bgResId = R.drawable.bg_item_swiping_active_state;
            }
            else if ((swipeState & Swipeable.STATE_FLAG_SWIPING) != 0)
            {
                bgResId = R.drawable.bg_item_swiping_state;
            }
            else
            {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemHorizontalSlideAmount(
                item.isPinned() ? Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getGroupItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;
        final View CheckBoxView = holder.mCheckBox;
        final View deleteItemView = holder.mDeleteItem;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY)
                && !ViewUtils.hitTest(CheckBoxView, x-offsetX, y-offsetY)
                && !ViewUtils.hitTest(deleteItemView, x-offsetX, y-offsetY);
    }

    @Override
    public boolean onCheckGroupCanStartDrag(MyGroupViewHolder holder, int groupPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckChildCanStartDrag(MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        /*
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
        */
        return false; //make children undragable
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(MyGroupViewHolder holder, int groupPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(MyChildViewHolder holder, int groupPosition, int childPosition) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        mProvider.moveGroupItem(fromGroupPosition, toGroupPosition);
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mProvider.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    @Override
    public int onGetGroupItemSwipeReactionType(MyGroupViewHolder holder, int groupPosition, int x, int y) {
        if (holder.tabID == 0)
            return Swipeable.REACTION_CAN_SWIPE_RIGHT;
        else
            return Swipeable.REACTION_CAN_SWIPE_LEFT;
        /*
        if (onCheckGroupCanStartDrag(holder, groupPosition, x, y)) {
            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
        */
    }

    @Override
    public int onGetChildItemSwipeReactionType(MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        if (onCheckChildCanStartDrag(holder, groupPosition, childPosition, x, y)) {
            return Swipeable.REACTION_CAN_NOT_SWIPE_BOTH_H;
        }
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetGroupItemSwipeBackground(MyGroupViewHolder holder, int groupPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                //bgResId = R.drawable.bg_swipe_group_item_left;
                bgResId = R.layout.bg_swipe_group_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_group_item_right;
                break;
        }
        TextDrawable td = new TextDrawable(holder.holderContext,holder.tabID);
        //holder.itemView.setBackgroundResource(R.);
        holder.itemView.setBackgroundColor(holder.holderContext.getResources().getColor(R.color.grey100));
        holder.itemView.setBackground(td);
    }

    @Override
    public void onSetChildItemSwipeBackground(MyChildViewHolder holder, int groupPosition, int childPosition, int type) {
        int bgResId = 0;
        switch (type) {
            case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_neutral;
                break;
            case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_left;
                break;
            case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgResId = R.drawable.bg_swipe_item_right;
                break;
        }

        holder.itemView.setBackgroundResource(bgResId);
    }

    @Override
    public SwipeResultAction onSwipeGroupItem(MyGroupViewHolder holder, int groupPosition, int result) {
        Log.d(TAG, "onSwipeGroupItem(groupPosition = " + groupPosition + ", result = " + result + ")");
        if (holder.tabID == 0 && result == Swipeable.RESULT_SWIPED_RIGHT) { //shopping cart tab
            return new GroupSwipeRightResultAction(this,groupPosition);
        }
        else if (holder.tabID == 1 && result == Swipeable.RESULT_SWIPED_LEFT) {
            return new GroupSwipeRightResultAction(this,groupPosition);
        }
        else
            return null;
    }

    @Override
    public SwipeResultAction onSwipeChildItem(MyChildViewHolder holder, int groupPosition, int childPosition, int result) {
        Log.d(TAG,"stop trying to swipe the children");
        return null;
        /*
        switch (result) {
            // swipe right
            case Swipeable.RESULT_SWIPED_RIGHT:
                if (mProvider.getChildItem(groupPosition, childPosition).isPinned()) {
                    // pinned --- back to default position
                    return new ChildUnpinResultAction(this, groupPosition, childPosition);
                } else {
                    // not pinned --- remove
                    return new ChildSwipeRightResultAction(this, groupPosition, childPosition);
                }
                // swipe left -- pin
            case Swipeable.RESULT_SWIPED_LEFT:
                return new ChildSwipeLeftResultAction(this, groupPosition, childPosition);
            // other --- do nothing
            case Swipeable.RESULT_CANCELED:
            default:
                if (groupPosition != RecyclerView.NO_POSITION) {
                    return new ChildUnpinResultAction(this, groupPosition, childPosition);
                } else {
                    return null;
                }
        }
        */
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private static class GroupSwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private boolean mSetPinned;

        GroupSwipeLeftResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractExpandableDataProvider.GroupData item =
                    mAdapter.mProvider.getGroupItem(mGroupPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.mExpandableItemManager.notifyGroupItemChanged(mGroupPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onGroupItemPinned(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class GroupSwipeRightResultAction extends SwipeResultActionRemoveItem {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;

        GroupSwipeRightResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeGroupItem(mGroupPosition);
            mAdapter.mExpandableItemManager.notifyGroupItemRemoved(mGroupPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onGroupItemRemoved(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class GroupUnpinResultAction extends SwipeResultActionDefault {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;

        GroupUnpinResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractExpandableDataProvider.GroupData item = mAdapter.mProvider.getGroupItem(mGroupPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.mExpandableItemManager.notifyGroupItemChanged(mGroupPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


    private static class ChildSwipeLeftResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;
        private boolean mSetPinned;

        ChildSwipeLeftResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractExpandableDataProvider.ChildData item =
                    mAdapter.mProvider.getChildItem(mGroupPosition, mChildPosition);

            if (!item.isPinned()) {
                item.setPinned(true);
                mAdapter.mExpandableItemManager.notifyChildItemChanged(mGroupPosition, mChildPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onChildItemPinned(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildSwipeRightResultAction extends SwipeResultActionRemoveItem {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;

        ChildSwipeRightResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeChildItem(mGroupPosition, mChildPosition);
            mAdapter.mExpandableItemManager.notifyChildItemRemoved(mGroupPosition, mChildPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mAdapter.mEventListener != null) {
                mAdapter.mEventListener.onChildItemRemoved(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class ChildUnpinResultAction extends SwipeResultActionDefault {
        private MyExpandableDraggableSwipeableItemAdapter mAdapter;
        private final int mGroupPosition;
        private final int mChildPosition;

        ChildUnpinResultAction(MyExpandableDraggableSwipeableItemAdapter adapter, int groupPosition, int childPosition) {
            mAdapter = adapter;
            mGroupPosition = groupPosition;
            mChildPosition = childPosition;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractExpandableDataProvider.ChildData item = mAdapter.mProvider.getChildItem(mGroupPosition, mChildPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.mExpandableItemManager.notifyChildItemChanged(mGroupPosition, mChildPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            mAdapter = null;
        }
    }
}
