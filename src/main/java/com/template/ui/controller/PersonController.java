package com.template.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.ContextLoader;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;

import com.template.model.domain.Person;
import com.template.model.service.PersonService;

public class PersonController extends GenericForwardComposer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9088508633791107170L;

	private PersonService personService;
	
	private AnnotateDataBinder binder;
	private Listbox personList;
	private Grid editPersonGrid;
	private Button createPerson;
	private Button updatePerson;
	private Button deletePerson;
	
	
	private Person person;
	
	
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		personService = (PersonService)ContextLoader.getCurrentWebApplicationContext().getBean("personService");
		binder = (AnnotateDataBinder)page.getAttribute("binder", false);
		person = new Person();
		personList.setModel(new ListModelList(personService.findAll()));
		personList.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem item, Object data, int arg2)
					throws Exception {
				// TODO Auto-generated method stub
				Person person = (Person) data;
				item.setValue(person);
				new Listcell(String.valueOf(person.getId())).setParent(item);
				new Listcell(person.getFirstName()).setParent(item);
				new Listcell(person.getLastName()).setParent(item);
			}
		});
		
	}
	
	private ListModelList getModel(){
		return (ListModelList) personList.getModel();
	}
	
	public void onClick$resetPerson(){
		personList.clearSelection();
		person = new Person();
		binder.loadComponent(editPersonGrid);
		createPerson.setDisabled(false);
		updatePerson.setDisabled(true);
		deletePerson.setDisabled(true);
	}
	
	public void onSelect$personList(){
		person = (Person)personList.getSelectedItem().getValue();
		binder.loadComponent(editPersonGrid);
		createPerson.setDisabled(true);
		updatePerson.setDisabled(false);
		deletePerson.setDisabled(false);
	}
	
	public void onClick$createPerson(){
		if(person.getFirstName() == null && person.getLastName() == null){
			Messagebox.show("no new content to add");
		}else{
			personService.save(person);
			getModel().add(person);
			
			clear();
		}
	}
	
	public void onClick$updatePerson(){
		Listitem listItem = personList.getSelectedItem();
		personService.save(person);
		listItem.setValue(person);
		int index = personList.getSelectedIndex();
		getModel().set(index, person);
	}
	
	private void clear(){
		person = new Person();
		binder.loadComponent(editPersonGrid);
	}
	
	public void onClick$deletePerson(){
		Messagebox.show("Are you sure to delete?", null, Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event event) throws Exception {
				if(event.getName().equals("onYes")) {
					getModel().remove(person);
					personService.delete(person);
					
					clear();
				}
			}
		});
	}
	
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
}
