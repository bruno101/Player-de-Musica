package view;

import java.awt.*;
import java.awt.Color;
import java.awt.Component;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.DefaultListCellRenderer;

//definimos essa classe para gerarmos uma lista transparente em que todos os elementos que foram clicados fiquem permanentemente coloridos
public class UserSongsListCellRenderer extends DefaultListCellRenderer {
	
	private HashSet<Integer> clickedSet = new HashSet<>();
	
	@Override
	public Component getListCellRendererComponent(JList <?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		setOpaque(isSelected);
		
		if (clickedSet.contains(index)) {

			setOpaque(true);
			setBackground(new Color(51,153,255));
			
		}
		
		return this;
		
	}
	
	//definimos essa funcao para podermos informar em 'View.java' quando um elemento for clicado, para que o 'renderer' possa pintá-los permanentemente
	public void addToClickedSet (Integer clickedIndex) {
		this.clickedSet.add(clickedIndex);
	}
	
	public void setClickedSet (HashSet<Integer> clickedSet) {
		this.clickedSet = clickedSet;
	}
	
	public void restart () {
		this.clickedSet = new HashSet<>();
	}
   
}
