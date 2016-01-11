package quokka.jellenberger.ogrocer;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

/**
 * Created by jellenberger on 12/15/15.
 */
public class ShoppingCartTabContent extends Fragment
        implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";

    public int _currentTab;
    public static int _tabID;
    static Context _activityContext;
    Drawable bulletIcon;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;

    public ShoppingCartDataProvider _dataProvider;
    public MyExpandableDraggableSwipeableItemAdapter mItemAdapter;

    public static ShoppingCartTabContent newInstance(int position) {
        ShoppingCartTabContent f = new ShoppingCartTabContent();

        Bundle args = new Bundle();
        args.putInt("tabID", position);
        f.setArguments(args);

        return f;
    }

    public int getTabID() {
        return getArguments().getInt("tabID", 0);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        _activityContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View contentView = inflater.inflate(R.layout.shopping_cart_frag_layout, container, false);
        _tabID = getTabID();
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _dataProvider = new ShoppingCartDataProvider(this);
        ShoppingCartView parent = (ShoppingCartView) getActivity();
        parent.registerDataProvider(0, _dataProvider);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.shopping_cart_recycler);
        mLayoutManager = new LinearLayoutManager(getContext());

        final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(eimSavedState);
        mRecyclerViewExpandableItemManager.setOnGroupExpandListener(this);
        mRecyclerViewExpandableItemManager.setOnGroupCollapseListener(this);

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        mItemAdapter = new MyExpandableDraggableSwipeableItemAdapter(mRecyclerViewExpandableItemManager, _dataProvider);

        mItemAdapter.setEventListener(new MyExpandableDraggableSwipeableItemAdapter.EventListener() {
            @Override
            public void onGroupItemRemoved(int groupPosition) {
                ((ShoppingCartView) getActivity()).onGroupItemSwipedOut(_tabID, groupPosition);
            }

            @Override
            public void onChildItemRemoved(int groupPosition, int childPosition) {}

            @Override
            public void onGroupItemPinned(int groupPosition) { }

            @Override
            public void onChildItemPinned(int groupPosition, int childPosition) { }

            @Override
            public void onItemViewClicked(View v, boolean pinned) {
                onItemViewClick(v, pinned);
            }
        });

        mAdapter = mItemAdapter;

        mWrappedAdapter = mRecyclerViewExpandableItemManager.createWrappedAdapter(mItemAdapter);       // wrap for expanding
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mWrappedAdapter);           // wrap for dragging
        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mWrappedAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        // Also need to disable them when using animation indicator.
        animator.setSupportsChangeAnimations(false);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setHasFixedSize(false);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));


        // NOTE:
        // The initialization order is very important! This order determines the priority of ` event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop > ExpandableItem
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

        // allow child (recycler) to handle touch events, or give them up to slidingtablayout when applicable
        ViewUtils.setTwoPane(mRecyclerView,_tabID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager.getSavedState());
        }
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager.release();
            mRecyclerViewSwipeManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager.release();
            mRecyclerViewExpandableItemManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {
    }

    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition);
        }
    }

    private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
        int childItemHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.list_item_height);
        int topMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
        int bottomMargin = topMargin; // bottom-spacing: 16dp

        mRecyclerViewExpandableItemManager.scrollToGroup(groupPosition, childItemHeight, topMargin, bottomMargin);
    }


    private void onItemViewClick(View v, boolean pinned) {
        final int flatPosition = mRecyclerView.getChildAdapterPosition(v);

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        final long expandablePosition = mRecyclerViewExpandableItemManager.getExpandablePosition(flatPosition);
        final int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        final int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            ((ShoppingCartView) getActivity()).onGroupItemClicked(groupPosition);
        } else {
            ((ShoppingCartView) getActivity()).onChildItemClicked(groupPosition, childPosition);
        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }


    public void notifyGroupItemRestored(int groupPosition) {
        //TODO: broken - null pointers everywhere
        mItemAdapter.notifyDataSetChanged();

        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);
        mRecyclerView.scrollToPosition(flatPosition);
    }

    public void notifyChildItemRestored(int groupPosition, int childPosition) {
        mAdapter.notifyDataSetChanged();

        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);
        mRecyclerView.scrollToPosition(flatPosition);
    }

    public void notifyGroupItemChanged(int groupPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);

        mAdapter.notifyItemChanged(flatPosition);
    }

    public void notifyChildItemChanged(int groupPosition, int childPosition) {
        final long expandablePosition = RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
        final int flatPosition = mRecyclerViewExpandableItemManager.getFlatPosition(expandablePosition);

        mAdapter.notifyItemChanged(flatPosition);
    }

    public AbstractExpandableDataProvider getDataProvider() {
        final Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DATA_PROVIDER);
        return ((ShoppingCartTabContent) fragment).getDataProvider();
    }

    public static void toggleCartItemChecked(View v){
        CheckBox cb = (CheckBox) v;
        View container = (View) v.getParent().getParent();
        TextView tv = (TextView) container.findViewById(R.id.recycler_item_text);
        if (cb.isChecked()){
            container.setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.grey200));
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setTextColor(ContextCompat.getColor(v.getContext(),R.color.primaryTextLight));
        }
        else {
            container.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.white));
            tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setTextColor(ContextCompat.getColor(v.getContext(), R.color.primaryText));
        }
    }
    public static void moveCartItemToSaved(int pos){
        ShoppingCartView scv = (ShoppingCartView) _activityContext;
        ShoppingCartDataProvider cartDP = (ShoppingCartDataProvider) scv.mDataProviders[0];
        final Pair<AbstractExpandableDataProvider.GroupData, List<AbstractExpandableDataProvider.ChildData>> item = cartDP.removeGroupItem2(pos);
        ShoppingCartDataProvider savedDP = (ShoppingCartDataProvider) scv.mDataProviders[1];
        savedDP.add(savedDP.getGroupCount(), item);
        ((SavedCartTabContent) savedDP.mOwnerFragment).mItemAdapter.notifyItemInserted(savedDP.getGroupCount());
    }
}
