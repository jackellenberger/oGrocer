package quokka.jellenberger.ogrocer;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jellenberger on 12/15/15.
 */
public class SavedCartTabContent extends Fragment
        implements RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener {

    private static final String FRAGMENT_TAG_DATA_PROVIDER = "data provider";
    private static final String FRAGMENT_LIST_VIEW = "list view";
    private static final String FRAGMENT_TAG_ITEM_PINNED_DIALOG = "item pinned dialog";
    private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";

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
    SearchBox mSearchBox;

    public static SavedCartTabContent newInstance(int position) {
        SavedCartTabContent f = new SavedCartTabContent();

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
        final View contentView = inflater.inflate(R.layout.saved_cart_frag_layout, container, false);

        mSearchBox = (SearchBox) contentView.findViewById(R.id.searchbox);
        mSearchBox.enableVoiceRecognition(this);
        // move the following code to wherever we interface with the local foods db
        List<String> knownFoods = ((ShoppingCartView) _activityContext).mCartItemDB.getAllNames();
        for(String f : knownFoods){
            SearchResult option = new SearchResult(f, getResources().getDrawable(R.drawable.ic_action_clock));
            mSearchBox.addSearchable(option);
        }
        mSearchBox.setLogoText("Save for later");
        //mSearchBox.setDrawerLogo(R.drawable.ic_action_add);

        mSearchBox.setMenuListener(new SearchBox.MenuListener(){
            @Override
            public void onMenuClick() {
                //Hamburger has been clicked
                Toast.makeText(_activityContext, "Menu click", Toast.LENGTH_LONG).show();
            }
        });

        mSearchBox.setSearchListener(new SearchBox.SearchListener(){
            @Override
            public void onSearchOpened() {
                //Use this to tint the screen
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onSearchClosed() {
                //Use this to un-tint the screen
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onSearchTermChanged(String term) {
                //React to the search term changing
                //Called after it has updated results
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onSearch(String searchTerm) {
                SavedCartTabContent.addItemToCart(searchTerm);
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onResultClick(SearchResult result) {
                //React to a result being clicked
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onSearchCleared() {
                //Called when the clear button is clicked
                ((ArrayAdapter)((ListView)mSearchBox.findViewById(R.id.results)).getAdapter()).notifyDataSetChanged();
            }
        });
        _tabID = getTabID();
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _dataProvider = new ShoppingCartDataProvider(this,1);
        ShoppingCartView parent = (ShoppingCartView) getActivity();
        parent.registerDataProvider(1, _dataProvider);

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.saved_cart_recycler);
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
            public void onGroupItemPinned(int groupPosition) {}
            @Override
            public void onChildItemPinned(int groupPosition, int childPosition) {}

            @Override
            public void onItemViewClicked(View v, boolean pinned) {}
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
        //mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getContext(), R.drawable.list_divider_h), true));


        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop > ExpandableItem
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewExpandableItemManager.attachRecyclerView(mRecyclerView);

        // allow child (recycler) to handle touch events, or give them up to slidingtablayout when applicable
        //ViewUtils.setTwoPane(mRecyclerView, _tabID);
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
        mAdapter.notifyDataSetChanged();

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
        return ((SavedCartTabContent) fragment).getDataProvider();
    }

    public static void movedSavedItemToCart(int pos){
        ShoppingCartView scv = (ShoppingCartView) _activityContext;
        ShoppingCartDataProvider savedDP = (ShoppingCartDataProvider) scv.mDataProviders[1];
        final Pair<AbstractExpandableDataProvider.GroupData, List<AbstractExpandableDataProvider.ChildData>> item = savedDP.removeGroupItem2(pos);
        ((ShoppingCartView) _activityContext).mCartItemDB.changeTab( (item.first).getText(), 0);
        ShoppingCartDataProvider cartDP = (ShoppingCartDataProvider) scv.mDataProviders[0];
        cartDP.add(cartDP.getGroupCount(), item);
        ((ShoppingCartTabContent) cartDP.mOwnerFragment).mItemAdapter.notifyItemInserted(cartDP.getGroupCount());
    }

    public static void addItemToCart(String itemName){
        ShoppingCartView scv = (ShoppingCartView) _activityContext;
        ShoppingCartDataProvider savedDP = (ShoppingCartDataProvider) scv.mDataProviders[1];
        final ShoppingCartDataProvider.ConcreteGroupData group = new ShoppingCartDataProvider.ConcreteGroupData(savedDP.getGroupCount()-1, itemName);
        final List<AbstractExpandableDataProvider.ChildData> children = new ArrayList<>();
        children.add(new ShoppingCartDataProvider.ConcreteChildData(savedDP.getGroupCount(), itemName));
        savedDP.add(savedDP.getGroupCount(),new Pair<AbstractExpandableDataProvider.GroupData, List<AbstractExpandableDataProvider.ChildData>>(group, children));
        ((SavedCartTabContent) savedDP.mOwnerFragment).mItemAdapter.notifyItemInserted(savedDP.getGroupCount());
    }

}
