package EWUPackage.panels.obj.material;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import EWUPackage.scene.*;
import EWUPackage.scene.primitives.*;
/**
 *
 * @author Don Bushnell
 * @version 6-Mar-2005
 */
public class MaterialLevels extends JPanel
{
	private static final long serialVersionUID = 1L;
	public int index=0;
	public int maxWidgets=56;
	public Object[] widgets = new Object[maxWidgets];
	public LevelsPanel ambient, diffuse,specular, emissive, reflectivity, refractivity;
	public Color disabledColor;
	boolean enabled=false;
	final MaterialEditor editor;
	boolean updating=false;

	public MaterialLevels(final MaterialEditor editor)
	{
		this.editor=editor;
		setPreferredSize(new Dimension(400,400));
		setBorder(new LineBorder(Color.DARK_GRAY, 2));
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		final double div=100.0;
		//final double div2=255.0;
		ambient = new LevelsPanel("Ambient", false, editor.editCanvas.material.ka);
		ambient.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color amb = JColorChooser.showDialog(null,
					"Ambient Color Component", ambient.button.getBackground());
				if (ambient != null) {
					editor.editCanvas.material.ka = new DoubleColor(amb);
					//editor.editCanvas.material.ka.r = amb.getRed() / div2;
					ambient.red.setValue((int)editor.editCanvas.material.ka.r * 100);
					//editor.editCanvas.material.ka.g = amb.getGreen() / div2;
					ambient.green.setValue((int)editor.editCanvas.material.ka.g * 100);
					//editor.editCanvas.material.ka.b = amb.getBlue() / div2;
					ambient.blue.setValue((int)editor.editCanvas.material.ka.b * 100);
					ambient.button.setBackground(amb);
					ambient.setButtonTextColor();
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		ambient.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = ambient.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						ambient.button.setBackground(new Color( ( (float) (source.
							getValue()) / 100.0f), floatVals[1], floatVals[2], floatVals[3]));
						ambient.setButtonTextColor();
						editor.editCanvas.material.ka.r = ( ( (double) (source.getValue()) / div));
						if(ambient.singleValue){
							ambient.green.setValue((int)(editor.editCanvas.material.ka.r * div + 0.5));
							editor.editCanvas.material.ka.g = editor.editCanvas.material.ka.r;
							ambient.blue.setValue((int)(editor.editCanvas.material.ka.r * div + 0.5));
							editor.editCanvas.material.ka.b = editor.editCanvas.material.ka.r;
						}
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		ambient.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = ambient.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						ambient.button.setBackground(new Color( floatVals[0], ((float) (source.getValue()) / 100.0f), 
								floatVals[2], floatVals[3]));
						//ambient.button.setBackground(new Color(tempColor.getRed(),
						//	( (int) source.getValue()) / 4, tempColor.getBlue()));
						editor.editCanvas.material.ka.g = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		ambient.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = ambient.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						ambient.button.setBackground(new Color( floatVals[0],floatVals[1], 
								((float) (source.getValue()) / 100.0f),  floatVals[3]));
						editor.editCanvas.material.ka.b = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});
		diffuse = new LevelsPanel("Diffuse", false, editor.editCanvas.material.kd);
		diffuse.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color dif = JColorChooser.showDialog(null,
					"Diffuse Color Component", diffuse.button.getBackground());
				if (dif != null) {
					editor.editCanvas.material.kd = new DoubleColor(dif);
					diffuse.red.setValue((int)(editor.editCanvas.material.kd.r * 100));
					//editor.editCanvas.material.kd.g = dif.getGreen() / div2;
					diffuse.green.setValue((int)(editor.editCanvas.material.kd.g * 100));
					//editor.editCanvas.material.kd.b = dif.getBlue() / div2;
					diffuse.blue.setValue((int)(editor.editCanvas.material.kd.b * 100));
					diffuse.button.setBackground(dif);
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		diffuse.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = diffuse.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						diffuse.button.setBackground(new Color( (float) (source.getValue() / 100.0f), 
								floatVals[1], floatVals[2],  floatVals[3]));
						editor.editCanvas.material.kd.r = ( ( (double) source.getValue()) / div);
						if(diffuse.singleValue){
							diffuse.green.setValue((int)(editor.editCanvas.material.kd.r * div + 0.5));
							editor.editCanvas.material.kd.g = editor.editCanvas.material.kd.r;
							diffuse.blue.setValue((int)(editor.editCanvas.material.kd.r * div + 0.5));
							editor.editCanvas.material.kd.b = editor.editCanvas.material.kd.r;
						}
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		diffuse.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = diffuse.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						diffuse.button.setBackground(new Color( floatVals[0], 
								(float) (source.getValue() / 100.0f),  floatVals[2], floatVals[3]));
						editor.editCanvas.material.kd.g = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		diffuse.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = diffuse.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						diffuse.button.setBackground(new Color( floatVals[0], 
								  floatVals[1], (float) (source.getValue() / 100.0f), floatVals[3]));
						editor.editCanvas.material.kd.b = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end methos stateChanged
		});
		specular = new LevelsPanel("Specular", false, editor.editCanvas.material.ks);
		specular.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color spec = JColorChooser.showDialog(null,
					"Specular Color Component", specular.button.getBackground());
				if (spec != null) {
					editor.editCanvas.material.ks = new DoubleColor(spec);
					
					specular.red.setValue((int)(editor.editCanvas.material.ks.r * 100));
					specular.green.setValue((int)(editor.editCanvas.material.ks.g * 100));
					specular.blue.setValue((int)(editor.editCanvas.material.ks.b * 100));
					specular.button.setBackground(spec);
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		specular.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = specular.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						specular.button.setBackground(new Color( (float) (source.getValue() / 100.0f), 
								floatVals[1], floatVals[2],  floatVals[3]));
						editor.editCanvas.material.ks.r = ( ( (double) source.getValue()) / div);
						if(specular.singleValue){
							specular.green.setValue((int)(editor.editCanvas.material.ks.r * div + 0.5));
							editor.editCanvas.material.ks.g = editor.editCanvas.material.ks.r;
							specular.blue.setValue((int)(editor.editCanvas.material.ks.r * div + 0.5));
							editor.editCanvas.material.ks.b = editor.editCanvas.material.ks.r;
						}					
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		specular.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = specular.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						specular.button.setBackground(new Color( floatVals[0], 
								(float) (source.getValue() / 100.0f),  floatVals[2], floatVals[3]));						
						editor.editCanvas.material.ks.g = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		specular.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = specular.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						specular.button.setBackground(new Color( floatVals[0], 
								  floatVals[1], (float) (source.getValue() / 100.0f), floatVals[3]));
						editor.editCanvas.material.ks.b = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});
		emissive = new LevelsPanel("Emissive", false,editor.editCanvas.material.emmColor );
		emissive.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color emm = JColorChooser.showDialog(null,
					"Emissive Color Component", ambient.button.getBackground());
				if (emm != null) {
					editor.editCanvas.material.emmColor= new DoubleColor(emm);
					emissive.red.setValue((int)(editor.editCanvas.material.emmColor.r * 100));
					emissive.green.setValue((int)(editor.editCanvas.material.emmColor.g * 100));
					emissive.blue.setValue((int)(editor.editCanvas.material.emmColor.b * 100));
					emissive.button.setBackground(emm);
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		emissive.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = emissive.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						emissive.button.setBackground(new Color( (float) (source.getValue() / 100.0f), 
								floatVals[1], floatVals[2],  floatVals[3]));
						editor.editCanvas.material.emmColor.r = ( ( (double)source.getValue()) / div);
						if(emissive.singleValue){
							emissive.green.setValue((int)(editor.editCanvas.material.emmColor.r * div + 0.5));
							editor.editCanvas.material.emmColor.g = editor.editCanvas.material.emmColor.r;
							emissive.blue.setValue((int)(editor.editCanvas.material.emmColor.r * div + 0.5));
							editor.editCanvas.material.emmColor.b = editor.editCanvas.material.emmColor.r;
						}					
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		emissive.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = emissive.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						emissive.button.setBackground(new Color( floatVals[0], 
								(float) (source.getValue() / 100.0f),  floatVals[2], floatVals[3]));						
						editor.editCanvas.material.emmColor.g = ( ( (double)source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		emissive.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = emissive.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						emissive.button.setBackground(new Color( floatVals[0], 
								  floatVals[1], (float) (source.getValue() / 100.0f), floatVals[3]));
						editor.editCanvas.material.emmColor.b = ( ( (double)source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});
		
		/*
		 * ***********************************************************
		 * Added By Johnathan Warner
		 * Reflection and Refraction Slider Controls
		 * ***********************************************************
		 * */
		
		reflectivity = new LevelsPanel("Reflectivity", true, editor.editCanvas.material.emmColor);
		reflectivity.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color refl = JColorChooser.showDialog(null,
					"Reflectivity", reflectivity.button.getBackground());
				if (reflectivity != null) {
					editor.editCanvas.material.reflectivity = new DoubleColor( refl);
					reflectivity.red.setValue((int)(editor.editCanvas.material.reflectivity.r * 100));
					reflectivity.green.setValue((int)(editor.editCanvas.material.reflectivity.g * 100));
					reflectivity.blue.setValue((int)(editor.editCanvas.material.reflectivity.b * 100));
					reflectivity.button.setBackground(refl);
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		reflectivity.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = reflectivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						reflectivity.button.setBackground(new Color( (float) (source.getValue() / 100.0f), 
									floatVals[1], floatVals[2],  floatVals[3]));
						editor.editCanvas.material.reflectivity.r = ( ( (double) source.getValue()) / div);
						if(reflectivity.singleValue){
							reflectivity.green.setValue((int)(editor.editCanvas.material.reflectivity.r * div + 0.5));
							editor.editCanvas.material.reflectivity.g = editor.editCanvas.material.reflectivity.r;
							reflectivity.blue.setValue((int)(editor.editCanvas.material.reflectivity.r * div + 0.5));
							editor.editCanvas.material.reflectivity.b = editor.editCanvas.material.reflectivity.r;
						}					
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		reflectivity.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = reflectivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						reflectivity.button.setBackground(new Color( floatVals[0], 
								(float) (source.getValue() / 100.0f),  floatVals[2], floatVals[3]));						
						editor.editCanvas.material.reflectivity.g = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		reflectivity.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = reflectivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						reflectivity.button.setBackground(new Color( floatVals[0], 
								  floatVals[1], (float) (source.getValue() / 100.0f), floatVals[3]));
						editor.editCanvas.material.reflectivity.b = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});
		
		
		refractivity = new LevelsPanel("Refractivity", true, editor.editCanvas.material.refractivity);
		refractivity.button.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//JColorChooser jcc = new JColorChooser();
				Color refr = JColorChooser.showDialog(null,
					"refractivity Color Component", refractivity.button.getBackground());
				if (refractivity != null) {
					refractivity.materialAccess = new DoubleColor( refr);
					refractivity.red.setValue((int)(editor.editCanvas.material.refractivity.r * 100));
					refractivity.green.setValue((int)(editor.editCanvas.material.refractivity.g * 100));
					refractivity.blue.setValue((int)(editor.editCanvas.material.refractivity.b * 100));
					refractivity.button.setBackground(refr);
					editor.updatePreview();
				} // end if
			} // end method mouseClicked
		});
		refractivity.red.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = refractivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						refractivity.button.setBackground(new Color( (float) (source.getValue() / 100.0f), 
								floatVals[1], floatVals[2],  floatVals[3]));
						editor.editCanvas.material.refractivity.r = ( ( (double) source.getValue()) / div);
						if(refractivity.singleValue){
							refractivity.green.setValue((int)(editor.editCanvas.material.refractivity.r * div + 0.5));
							editor.editCanvas.material.refractivity.g = editor.editCanvas.material.refractivity.r;
							refractivity.blue.setValue((int)(editor.editCanvas.material.refractivity.r * div + 0.5));
							editor.editCanvas.material.refractivity.b = editor.editCanvas.material.refractivity.r;
						}					
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		refractivity.green.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = refractivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						refractivity.button.setBackground(new Color( floatVals[0], 
								(float) (source.getValue() / 100.0f),  floatVals[2], floatVals[3]));						
						editor.editCanvas.material.refractivity.g = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});

		refractivity.blue.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!updating) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						Color tempColor = refractivity.button.getBackground();
						float [] floatVals = new float[4];
						floatVals = tempColor.getComponents(null);
						refractivity.button.setBackground(new Color( floatVals[0], 
								  floatVals[1], (float) (source.getValue() / 100.0f), floatVals[3]));
						editor.editCanvas.material.refractivity.b = ( ( (double) source.getValue()) / div);
						editor.updatePreview();
					} // end if
				} // end if
			} // end method stateChanged
		});
		
		/*
		 * *****************************************************************************
		 * *****************************************************************************
		 * *****************************************************************************
		 * *****************************************************************************
		 */
		
		
		disabledColor=ambient.button.getBackground();
		gbc.weightx= 0.5;
		gbc.weighty= 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx=0;
		gbc.gridy=0;
		add(ambient, gbc);
		gbc.gridx=1;
		add(diffuse, gbc);
		gbc.gridx=2;
		add(reflectivity, gbc);
		gbc.gridx=0;
		gbc.gridy=1;
		add(specular, gbc);
		gbc.gridx=1;
		add(emissive, gbc);
		gbc.gridx=2;
		add(refractivity, gbc);
	} // end constructor

	public class LevelsPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;
		JButton button;
		JCheckBox singleValueCB;
		boolean singleValue;
		LevelsSlider red;
		LevelsSlider green;
		LevelsSlider blue;
		DoubleColor materialAccess;
		Color buttonTextColor = Color.black;

		public LevelsPanel(String name, boolean oneValue, DoubleColor matAccess)
		{
			singleValue =oneValue;
			materialAccess = matAccess;
			setBorder(new EtchedBorder());
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			button = new JButton(name);
			button.setFocusPainted(false);
			//button.setForeground(buttonTextColor);
			widgets[index++]=button;
			setButtonTextColor();

			red = new LevelsSlider();
			green = new LevelsSlider();
			blue = new LevelsSlider();

			singleValueCB = new JCheckBox("One Value", oneValue);
			singleValueCB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e){
					int redVal;
					int state = e.getStateChange();
					if(state == ItemEvent.SELECTED){
						redVal = red.getValue();
						green.setValue(redVal);
						blue.setValue(redVal);
						green.setEnabled(false);
						blue.setEnabled(false);
						materialAccess.g = materialAccess.r;
						materialAccess.b = materialAccess.r;
						singleValue = true;
						editor.updatePreview();
					}
					else {
						green.setEnabled(true);
						blue.setEnabled(true);
						redVal = red.getValue();
						green.setValue(redVal);
						blue.setValue(redVal);
						singleValue = false;
					}
				}
			});
			gbc.gridx=0;
			gbc.gridy=0;		
			gbc.weightx= 0.5;
			gbc.weighty= 0.1;

			gbc.gridwidth=3;
			gbc.fill=GridBagConstraints.HORIZONTAL;
			add(button, gbc);

			//gbc.fill=GridBagConstraints.NONE;
			gbc.fill=GridBagConstraints.VERTICAL;
			gbc.weighty = 1.0;
			gbc.gridy=1;
			gbc.gridwidth=1;
			add(red, gbc);
			gbc.gridx=1;
			add(green, gbc);
			gbc.gridx=2;
			add(blue, gbc);
			JLabel redLabel = new JLabel("R");
			widgets[index++]=redLabel;
			gbc.weighty= 0.1;
			gbc.gridx=0;
			gbc.gridy=2;
			add(redLabel, gbc);
			JLabel greenLabel = new JLabel("G");
			widgets[index++]=greenLabel;
			gbc.gridx=1;
			add(greenLabel, gbc);
			JLabel blueLabel = new JLabel("B");
			widgets[index++]=blueLabel;
			gbc.gridx=2;
			add(blueLabel, gbc);
			// ADD checkbox in gridy = 3 with gridwidth = 3
			widgets[index++]=singleValueCB;
			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 3;
			add(singleValueCB, gbc);
			if(oneValue){
				green.setEnabled(false);
				blue.setEnabled(false);				
			}
		} // end constructor

		public void updateLevels(DoubleColor color)
		{
			button.setBackground(new Color((float)color.r, (float)color.g, (float)color.b, (float)color.a));
			double avgCol = (color.r + color.g + color.b)/3.0;
			if (avgCol < 0.75)
				button.setForeground(Color.white);
			else
				button.setForeground(Color.black);
			if (enabled) {
				red.setValue((int)(color.r *100.0));
				green.setValue((int)(color.g * 100.0));
				blue.setValue((int)(color.b * 100.0));
				if(singleValue){
					green.setEnabled(false);
					blue.setEnabled(false);
				}
				else{
					//editor.editCanvas.material.reflectivity.g = ( ( (double) source.getValue()) / div);
					green.setEnabled(true);
					blue.setEnabled(true);				
				}
			} // end if
			else {
				red.setValue(0);
				green.setValue(0);
				blue.setValue(0);
			} // end else
		} // end method updateLevels
		
		public void setButtonTextColor(){
			double avg = (materialAccess.r + materialAccess.g + materialAccess.b) / 3.0;
			if(avg < 0.5){
				button.setForeground(Color.white);
				buttonTextColor = Color.white;
			}
			else{
				button.setForeground(Color.black);
				buttonTextColor = Color.black;
			}
		}
		
	} // end class LevelsPanel

	public class LevelsSlider extends JSlider
	{
		private static final long serialVersionUID = 1L;

		public LevelsSlider()
		{
			super(JSlider.VERTICAL , 0, 100, 0);
			this.setPreferredSize(new Dimension(30,100));
			setMajorTickSpacing(10);
			setMinorTickSpacing(0);
			setPaintTicks(true);
			setPaintLabels(false);
			setFocusable(false);
			widgets[index++]=this;
		} // end constructor
	} // end class LevelsSlider

	public void setEnabled(boolean bool)
	{
		for(int i=0; i<index; i++)
			((JComponent)widgets[i]).setEnabled(bool);
	} // end method setEnabled

	public void updateLevels(MaterialCell matCell)
	{
		updating=true;
		if(matCell==null)
		{
			enabled=false;
			//ambient.updateLevels(disabledColor);
			//diffuse.updateLevels(disabledColor);
			//emissive.updateLevels(disabledColor);
			//specular.updateLevels(disabledColor);
			//refractivity.updateLevels(disabledColor);
			//reflectivity.updateLevels(disabledColor);
		} // end if
		else
		{
			enabled=true;
			//System.out.println("ka"+ matCell.ka.toString()+" "+matCell.refractivityIndex+" "+matCell.shiny);
			ambient.updateLevels(matCell.ka);
			diffuse.updateLevels(matCell.kd);
			specular.updateLevels(matCell.ks);
			emissive.updateLevels(matCell.emmColor);
			refractivity.updateLevels(matCell.refractivity);
			reflectivity.updateLevels(matCell.reflectivity);
		} // end else
		updating=false;
	} // end method updateLevels

} // end class MaterialLevels
