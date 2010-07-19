package aeminiumruntime.queue;

public class QTaskList {
	// global 'null' object
	static public final QTaskList NO_LIST = new QTaskList("NO_LIST");
	public static final QListItem NO_ITEM = new QListItem() {

		@Override
		public void setPrevItem(QListItem prev) {}
		
		@Override
		public void setNextItem(QListItem next) {}
		
		@Override
		public QListItem getPrevItem() {
			return NO_ITEM;
		}
		
		@Override
		public QListItem getNextItem() {
			return NO_ITEM;
		}
		
		@Override
		public QTaskList getTaskList() {
			return QTaskList.NO_LIST;
		}
		
		@Override
		public void setTaskList(QTaskList list){}
		
		@Override 
		public String toString() {
			return "NO_ITEM";
		}
	}; 
	
	
	// instance members 
	private QListItem  head = NO_ITEM;
	private String name;
	
	public QTaskList(String name) {
		this.name = name;
	}
	
	public void addListItem(QListItem item) {
		assert(item.getTaskList() == NO_LIST );
		synchronized (this) {
			if ( head == NO_ITEM ) {
				head = item;
				item.setTaskList(this);
			} else {
				head.setPrevItem(item);
				item.setNextItem(head);
				head = item;
			}
		}
	}

	public void removeListItem(QListItem item) {
		assert(item.getTaskList() == this);
		synchronized (this) {
			if ( item == head ){
				head = head.getNextItem();
				if ( head != NO_ITEM ) {
					head.setPrevItem(NO_ITEM);
				}
				item.setNextItem(NO_ITEM);
			} else {
				QListItem prev = item.getPrevItem();
				QListItem next = item.getNextItem();
				prev.setNextItem(next);
				next.setPrevItem(prev);
				item.setPrevItem(NO_ITEM);
				item.setNextItem(NO_ITEM);
			}
			item.setTaskList(NO_LIST);
		}
	}
	
	public boolean isEmpty() {
		return (head == NO_ITEM)?true:false;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
