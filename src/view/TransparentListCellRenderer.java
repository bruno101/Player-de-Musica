package view;

import java.awt.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.DefaultListCellRenderer;

//definimos essa classe para gerarmos uma lista com células transparentes 
public class TransparentListCellRenderer extends DefaultListCellRenderer {
	
	private int currentSongIndex = 0;
	
	@Override
	public Component getListCellRendererComponent(JList <?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		setOpaque(isSelected);
		
		if (index == this.currentSongIndex) {
			
			setOpaque(true);
			setBackground(new Color(51,153,255));
			
		}
		
		return this;
		
	}
	
	//definimos essa funcao para que a lista destaque a musica em execucao no momento
	public void setCurrentSongIndex (int index) {
		this.currentSongIndex = index;
	}
	
}
