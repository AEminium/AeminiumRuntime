package aeminiumruntime.queue;

public interface QListItem {
	public QTaskList getTaskList();	
	public QListItem getPrevItem();
	public QListItem getNextItem();
	public void setPrevItem(QListItem prev);
	public void setNextItem(QListItem next);
	public void setTaskList(QTaskList list);
}
