package org.jocean.zookeeper.webui.admin;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

import rx.functions.Action0;

public class NodeContent {
    NodeContent(
            final String title, 
            final Tabs tabs, 
            final Tabpanels tabpanels) {
        this._title = title;
        this._tab = new Tab(title) {
            void doClose() {
                super.close();
                if (null!= _onClose) {
                    _onClose.call();
                }
            }
            
            /* (non-Javadoc)
             * @see org.zkoss.zul.Tab#close()
             */
            @Override
            public void close() {
                if (isModified()) {
                    Messagebox.show("Content has modified, Are you sure to discard?", "Confirm Dialog", 
                            Messagebox.OK | Messagebox.CANCEL, 
                            Messagebox.QUESTION, 
                            new EventListener<Event>() {
                        public void onEvent(Event evt) throws InterruptedException {
                            if (evt.getName().equals("onOK")) {
                                doClose();
                            }
                        }});
                } else {
                    doClose();
                }
            }
            private static final long serialVersionUID = 1L;
            {
                this.setClosable(true);
            }};
        tabs.appendChild(this._tab);
        this._apply = new Menuitem("Apply") {
            private static final long serialVersionUID = 1L;
        {
            this.setDisabled(true);
        }};
        this._apply.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doApply();
            }});
        this._tabpanel = new Tabpanel() {
            private static final long serialVersionUID = 1L; {
                this.appendChild(new Menubar() {
                    private static final long serialVersionUID = 1L;
                    {
                        this.appendChild(_apply);
                    }
                });
            }};
        tabpanels.appendChild(this._tabpanel);
        this._tab.setSelected(true);
    }
    
    public NodeContent setOnClose(final Action0 onClose) {
        this._onClose = onClose;
        return this;
    }
    
    public NodeContent setOnApply(final Action0 onApply) {
        this._onApply = onApply;
        return this;
    }
    
    public NodeContent appendToTab(final Component component) {
        this._tabpanel.appendChild(component);
        return this;
    }
    
    public void select() {
        this._tab.setSelected(true);
    }

    private boolean isModified() {
        return this._isModified;
    }
    
    public void markModified() {
        if (!this._isModified) {
            this._isModified = true;
            this._tab.setLabel(this._title + " *");
            this._apply.setDisabled(false);
        }
    }
    
    private void doApply() {
        if (null!=this._onApply) {
            this._onApply.call();
        }
        this._isModified = false;
        this._tab.setLabel(this._title);
        this._apply.setDisabled(true);
    }

    private final String _title;
    private final Tab _tab;
    private final Tabpanel _tabpanel;
    private final Menuitem _apply;
    private boolean _isModified = false;
    private Action0 _onClose = null;
    private Action0 _onApply = null;
}
