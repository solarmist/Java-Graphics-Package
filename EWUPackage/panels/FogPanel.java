package EWUPackage.panels;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import EWUPackage.scene.*;

/**
 * This class provides a panel for manipulating fog settings.
 *
 * @author Russell Valentine
 * @version Spring 2004
 */

public class FogPanel extends MasterPanel
{
  private static final long serialVersionUID = 1L;
  public boolean fogEnabled; // true if fog is enabled
  public boolean clearColor; // true if clear color is enabled
  public JCheckBox enableFog; // toggles fog on and off
  public JCheckBox setClearColor; // toggles clear color on and off

  public JPanel color; // color control panel
  public JSlider red;
  public JLabel lab_red;
  public JFormattedTextField redVal;
  public JSlider green;
  public JLabel lab_green;
  public JFormattedTextField greenVal;
  public JSlider blue;
  public JLabel lab_blue;
  public JFormattedTextField blueVal;
  public JSlider alpha;
  public JLabel lab_alpha;
  public JFormattedTextField alphaVal;

  public JPanel equation; // equation panel
  public ButtonGroup equBG;
  public JRadioButton linear;
  public JRadioButton exp;
  public JRadioButton exp2;

  public JPanel hint; // hint panel
  public ButtonGroup hintBG;
  public JRadioButton dontCare;
  public JRadioButton nicest;
  public JRadioButton fastest;

  public JPanel vars; // variable panel
  public JSlider density;
  public JLabel lab_density;
  public JFormattedTextField densityVal;
  public JLabel lab_start;
  public JSlider start;
  public JFormattedTextField startVal;
  public JLabel lab_end;
  public JSlider end;
  public JFormattedTextField endVal;

  public float far;

  public FogPanel(Scene sceneRef)
  {
	super(sceneRef);
	name = "Fog";
	
    this.far = 1000.0f; // sets the far distance
    GridBagConstraints gBc;
    this.theScene = sceneRef;
    this.setLayout(new GridBagLayout());
    gBc = new GridBagConstraints();

    // The making of the fog enable check box
    this.enableFog = new JCheckBox("Enable Fog Effect", false);
    this.theScene.fog = (false);
    gBc.anchor = GridBagConstraints.WEST;
    gBc.insets = new Insets(10, 5, 0, 0);
    gBc.gridx = 0;
    gBc.gridy = 0;
    this.add(this.enableFog, gBc);
    this.enableFog.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			fogEnabled = enableFog.isSelected();
			theScene.fog = (fogEnabled);
			setSettingsEnabled(fogEnabled);
			if(!fogEnabled)
				theScene.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			else if( setClearColor.isSelected() ) {
				float r = (float)red.getValue()/256;
				float g = (float)green.getValue()/256;
				float b = (float)blue.getValue()/256;
				float alpha = 1.0f;
          		theScene.setClearColor(r,g,b,alpha);
			}
			theScene.refreshCanvas();
		} // end actionPerformed
	});

    // the creation of the clear color check box
    gBc = new GridBagConstraints();
    gBc.anchor = GridBagConstraints.WEST;
    gBc.insets = new Insets(5, 5, 5, 0);
    gBc.gridx = 0;
    gBc.gridy = 1;
    this.setClearColor = new JCheckBox("Set Clear Color", false);
    this.add(this.setClearColor, gBc);
    this.setClearColor.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			clearColor = setClearColor.isSelected();
			if(clearColor) {
				float r = (float)red.getValue()/256;
				float g = (float)green.getValue()/256;
				float b = (float)blue.getValue()/256;
				float alpha = 1.0f;
          		theScene.setClearColor(r,g,b,alpha);
			}
			else {
				theScene.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			}
			theScene.refreshCanvas();
		} // end actionPerformed
	});

    //the color panel
    this.color = new JPanel(new GridBagLayout());
    this.color.setBorder(new TitledBorder("Color"));
    gBc = new GridBagConstraints();
    gBc.gridx = 1;
    gBc.gridy = 0;
    gBc.gridheight = 1;
    gBc.gridwidth = 1;
    gBc.weightx = 0.25;
    gBc.weighty = 1.0;
    gBc.fill = GridBagConstraints.BOTH;
    this.red = new JSlider(JSlider.VERTICAL, 0, 256, 0);
    this.red.setBackground(Color.red);
    this.red.setName("red");
    this.theScene.Fred = (0);
    this.red.addChangeListener(new SliderListener());
    this.color.add(this.red, gBc);
    this.red.addMouseWheelListener( new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() > 0)
				red.setValue(e.getScrollAmount()+red.getValue());
			else
				red.setValue(red.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
    });

    gBc.gridx = 3;
    gBc.gridy = 0;
    this.green = new JSlider(JSlider.VERTICAL, 0, 256, 0);
    this.green.setBackground(Color.green);
    this.theScene.Fgreen = (0);
    this.green.setName("green");
    this.green.addChangeListener(new SliderListener());
    this.color.add(this.green, gBc);
    this.green.addMouseWheelListener( new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() > 0)
				green.setValue(e.getScrollAmount() + green.getValue());
			else
				green.setValue(green.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
    });

    gBc.gridx = 5;
    gBc.gridy = 0;
    this.blue = new JSlider(JSlider.VERTICAL, 0, 256, 0);
    this.blue.setBackground(Color.blue);
    this.blue.setValue(128);
    this.theScene.Fblue = (128);
    this.blue.setName("blue");
    this.blue.addChangeListener(new SliderListener());
    this.color.add(this.blue, gBc);
    this.blue.addMouseWheelListener( new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() > 0)
				blue.setValue(e.getScrollAmount()+ blue.getValue());
			else
				blue.setValue(blue.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
    });

    gBc.gridx = 7;
    gBc.gridy = 0;
    this.alpha = new JSlider(JSlider.VERTICAL, 0, 100, 0);
    this.alpha.setBorder(new LineBorder(Color.GRAY, 3, true));
    this.theScene.Falpha = (1);
    this.alpha.setName("alpha");
    this.alpha.addChangeListener(new SliderListener());
    this.color.add(this.alpha, gBc);
    this.alpha.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getWheelRotation() > 0)
				alpha.setValue(e.getScrollAmount() + alpha.getValue());
			else
				alpha.setValue(alpha.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
    });

	gBc.insets = new Insets(5,5,5,0);
	gBc.weighty = 0.0;
    gBc.gridx = 0;
    gBc.gridy = 1;
    gBc.weightx = 0.0;
    //this.lab_red = new JLabel(" Red:");
    //this.color.add(this.lab_red, gBc);

    java.text.NumberFormat numberFormatColor = java.text.NumberFormat.getIntegerInstance();
    NumberFormatter formatterC = new NumberFormatter(numberFormatColor);
    formatterC.setMinimum(new Integer(0));
    formatterC.setMaximum(new Integer(256));

    gBc.gridx = 1;
    gBc.weightx = 0.25;
    this.redVal = new JFormattedTextField(formatterC);
    this.redVal.setColumns(3);
    this.redVal.setValue(new Integer(0));
    this.redVal.setHorizontalAlignment(JTextField.CENTER);
    this.redVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.redVal.getActionMap().put("check", new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(!redVal.isEditValid()) {
				redVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
        else try {
			redVal.commitEdit();
			int x = Integer.parseInt(redVal.getText());
			red.setValue(x);
			theScene.Fred = (x);
			if (clearColor) {
				float r = (float) red.getValue() / 256;
				float g = (float) green.getValue() / 256;
				float b = (float) blue.getValue() / 256;
				float alpha = 1.0f;
				theScene.setClearColor(r, g, b, alpha);
			} // end if
			theScene.refreshCanvas();
		} // end try
		catch (java.text.ParseException exc) {}
      } // end actionPerformed
    });
    this.color.add(this.redVal, gBc);

    gBc.gridx = 2;
    gBc.weightx = 0.0;
    //this.lab_green = new JLabel(" Green:");
    //this.color.add(this.lab_green, gBc);

    gBc.gridx = 3;
    gBc.weightx = 0.25;
    this.greenVal = new JFormattedTextField(formatterC);
    this.greenVal.setValue(new Integer(0));
    this.greenVal.setColumns(3);
    this.greenVal.setHorizontalAlignment(JTextField.CENTER);
    this.greenVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.greenVal.getActionMap().put("check", new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(!greenVal.isEditValid()) {
				greenVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else try
			{
				greenVal.commitEdit();
				int x = Integer.parseInt(greenVal.getText());
				green.setValue(x);
				theScene.Fgreen = (x);
				if(clearColor)
				{
					float r = (float) red.getValue() / 256;
					float g = (float) green.getValue() / 256;
					float b = (float) blue.getValue() / 256;
					float alpha = 1.0f;
					theScene.setClearColor(r, g, b, alpha);
				} // end if
				theScene.refreshCanvas();
			} // end try
			catch(java.text.ParseException exc){}
		} // end actionPerformed
    });

    this.color.add(this.greenVal, gBc);

    gBc.gridx = 4;
    gBc.weightx = 0.0;
    //this.lab_blue = new JLabel(" Blue:");
    //this.color.add(this.lab_blue, gBc);

    gBc.gridx = 5;
    gBc.weightx = 0.25;
    this.blueVal = new JFormattedTextField(formatterC);
    this.blueVal.setValue(new Integer(0));
    this.blueVal.setColumns(3);
    this.blueVal.setHorizontalAlignment(JTextField.CENTER);
    this.blueVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.blueVal.getActionMap().put("check", new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(!blueVal.isEditValid()) {
				blueVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else try
			{
				blueVal.commitEdit();
				int x = Integer.parseInt(blueVal.getText());
				blue.setValue(x);
				theScene.Fblue = (x);
				if(clearColor)
				{
					float r = (float) red.getValue() / 256;
					float g = (float) green.getValue() / 256;
					float b = (float) blue.getValue() / 256;
					float alpha = 1.0f;
					theScene.setClearColor(r, g, b, alpha);
				} // end if
				theScene.refreshCanvas();
			} // end try
			catch(java.text.ParseException exc){}
		} // end actionPerformed
    });
    this.color.add(this.blueVal, gBc);

    gBc.gridx = 6;
    gBc.weightx = 0.0;
    //this.lab_alpha = new JLabel(" Alpha:");
    //this.color.add(this.lab_alpha, gBc);

    java.text.DecimalFormat decFormat2 = new java.text.DecimalFormat("#0.00");
    NumberFormatter decF = new NumberFormatter(decFormat2);
    decF.setMinimum(new Double(0.00d));
    decF.setMaximum(new Double(1.00d));

    gBc.gridx = 7;
    gBc.weightx = 0.25;
    this.alphaVal = new JFormattedTextField(decF);
    this.alphaVal.setColumns(4);
    this.alphaVal.setValue(new Double(0.00d));
    this.alphaVal.setHorizontalAlignment(JFormattedTextField.CENTER);
    this.color.add(this.alphaVal, gBc);
    this.alphaVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.alphaVal.getActionMap().put("check", new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(!alphaVal.isEditValid()) {
				alphaVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else try
			{
				alphaVal.commitEdit();
				float Falpha = Float.parseFloat(alphaVal.getText());
				start.setValue((int)(Falpha*100));
				theScene.Falpha = (Falpha);
				theScene.refreshCanvas();
			} // end try
			catch(java.text.ParseException exc){}
		} // end actionPerformed
    });

	gBc.insets = new Insets(0,0,0,0);
	gBc.weighty = 1.0;
    gBc.gridx = 0;
    gBc.gridy = 2;
    gBc.fill = GridBagConstraints.BOTH;
    this.add(this.color, gBc);

    gBc = new GridBagConstraints();
    this.equation = new JPanel();
    this.equation.setBorder(new TitledBorder("Equation"));
    this.equBG = new ButtonGroup();
    this.linear = new JRadioButton("GL_LINEAR",true);
    this.theScene.Fequ = ('l');
    this.exp = new JRadioButton("GL_EXP");
    this.exp2 = new JRadioButton("GL_EXP2");
    this.equBG.add(this.linear);
    this.equBG.add(this.exp);
    this.equBG.add(this.exp2);
    this.equation.setLayout(new GridLayout(3,1));
    this.equation.add(this.linear);
    this.equation.add(this.exp);
    this.equation.add(this.exp2);
    gBc.gridx = 0;
    gBc.gridy = 3;
    gBc.fill = GridBagConstraints.BOTH;
    this.add(this.equation, gBc);
    this.linear.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			theScene.Fequ = ('l');
			// enable the Start and End GUI settings
			setStartEndEnabled(true);
			setDensityEnabled(false);
			theScene.refreshCanvas();
		} // end actionPerformed
    });
    this.exp.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent evt) {
			theScene.Fequ = ('e');
			setStartEndEnabled(false);
			setDensityEnabled(true);
			theScene.refreshCanvas();
		} // end actionPerformed
    });
    this.exp2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent evt) {
			theScene.Fequ = ('f');
			setStartEndEnabled(false);
			setDensityEnabled(true);
			theScene.refreshCanvas();
		} // end actionPerformed
    });

    gBc = new GridBagConstraints();
    this.hint = new JPanel();
    this.hint.setBorder(new TitledBorder("Hint"));
    this.hint.setLayout(new GridLayout(3,1));
    this.hintBG = new ButtonGroup();
    this.nicest = new JRadioButton("GL_NICEST");
    this.hintBG.add(this.nicest);
    this.dontCare = new JRadioButton("GL_DONT_CARE", true);
    this.theScene.Fhint = ('d');
    this.hintBG.add(this.dontCare);
    this.fastest = new JRadioButton("GL_FASTEST");
    this.hintBG.add(this.fastest);
    this.hint.add(this.dontCare);
    this.hint.add(this.fastest);
    this.hint.add(this.nicest);
    gBc.gridx = 0;
    gBc.gridy = 7;
    gBc.fill = GridBagConstraints.BOTH;
    this.add(this.hint, gBc);
	this.dontCare.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			theScene.Fhint = ('d');
			theScene.refreshCanvas();
		} // end actionPerformed
	});
	this.nicest.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			theScene.Fhint = ('n');
			theScene.refreshCanvas();
		} // end actionPerformed
	});
	this.fastest.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			theScene.Fhint = ('f');
			theScene.refreshCanvas();
		} // end actionPerformed
	});

    gBc = new GridBagConstraints();
    this.vars = new JPanel(new GridBagLayout());
    this.vars.setBorder(new TitledBorder("Variables"));

	gBc.fill = GridBagConstraints.HORIZONTAL;
	gBc.weightx = 1.0;
	gBc.insets = new Insets(5,5,5,0);
    gBc.gridx = 0;
    gBc.gridy = 0;
    gBc.gridwidth = 4;
    this.density = new JSlider(0,300);
    this.density.setValue(100);
    this.density.setName("density");
    this.density.addChangeListener(new SliderListener());
    this.vars.add(this.density, gBc);
	this.density.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0)
				density.setValue(e.getScrollAmount() + density.getValue());
			else
				density.setValue(density.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
	});

    gBc.gridx = 1;
    gBc.gridy = 1;
    gBc.gridwidth = 1;
    gBc.weightx = 0.0;
    gBc.insets = new Insets(0,5,15,0);
    this.lab_density = new JLabel("Density: ");
    this.vars.add(this.lab_density, gBc);

    gBc.gridx = 2;
    gBc.gridy = 1;
	gBc.fill = GridBagConstraints.NONE;
    java.text.DecimalFormat decFormat = new java.text.DecimalFormat("#0.00");
    NumberFormatter f2 = new NumberFormatter(decFormat);
    f2.setMinimum(new Double(0.00d));
    f2.setMaximum(new Double(3.00d));

    this.densityVal = new JFormattedTextField(f2);
    this.densityVal.setValue(new Double(1.00d));
    this.densityVal.setColumns(4);
    this.theScene.Fdensity = (1.00d);
    this.densityVal.setHorizontalAlignment(JFormattedTextField.CENTER);
    this.vars.add(this.densityVal, gBc);
    this.densityVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.densityVal.getActionMap().put("check", new AbstractAction(){
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (!densityVal.isEditValid()) {
				densityVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else
				try {
					densityVal.commitEdit();
					double Fdensity = Double.parseDouble(densityVal.getText());
					density.setValue( (int) ( (Fdensity) * 100));
					theScene.Fdensity = (Fdensity);
					theScene.refreshCanvas();
				} // end try
				catch (java.text.ParseException exc) {}
		} // end actionPerformed
	});

    gBc.gridx = 0;
    gBc.gridy = 3;
    gBc.gridwidth = 4;
	gBc.insets = new Insets(5,5,5,0);
	gBc.fill = GridBagConstraints.HORIZONTAL;
    this.start = new JSlider(0,10000);
    this.start.setValue(0);
    this.theScene.Fstart = (0);
    this.start.setName("start");
    this.start.addChangeListener(new SliderListener());
    this.vars.add(this.start, gBc);
	this.start.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0)
				start.setValue(e.getScrollAmount() + start.getValue());
			else
				start.setValue(start.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
	});

    gBc.gridx = 1;
    gBc.gridy = 4;
    gBc.gridwidth = 1;
    gBc.weightx = 0.0;
    gBc.insets = new Insets(0,5,15,0);
    this.lab_start = new JLabel("Start: ");
    this.vars.add(this.lab_start, gBc);

    java.text.NumberFormat decFormat3 = new java.text.DecimalFormat("#000.00");
    NumberFormatter formatter = new NumberFormatter(decFormat3);

    gBc.gridx = 2;
    gBc.gridy = 4;
    gBc.fill = GridBagConstraints.NONE;
    formatter.setMinimum(new Float(0.00f));
    formatter.setMaximum(new Float(100.00f));
    this.startVal = new JFormattedTextField(formatter);
    this.startVal.setValue(new Float(0));
    this.startVal.setColumns(5);
    this.startVal.setHorizontalAlignment(JFormattedTextField.CENTER);
    this.vars.add(this.startVal, gBc);
    this.startVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.startVal.getActionMap().put("check", new AbstractAction(){
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (!startVal.isEditValid()) {
				startVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else
				try {
					startVal.commitEdit();
					float Fstart = Float.parseFloat(startVal.getText());
					start.setValue( (int) (Fstart * 100));
					theScene.Fstart = (Fstart);
					theScene.refreshCanvas();
				} // end try
				catch (java.text.ParseException exc) {}
		} // end actinoPerformed
	});

    gBc.gridx = 0;
    gBc.gridy = 6;
    gBc.gridwidth = 4;
	gBc.insets = new Insets(5,5,5,0);
	gBc.fill = GridBagConstraints.HORIZONTAL;
    this.end = new JSlider(0,10000);
    this.end.setValue(2500);
    this.theScene.Fend = (25);
    this.end.setName("end");
    this.end.addChangeListener(new SliderListener());
    this.vars.add(this.end, gBc);
	this.end.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() > 0)
				end.setValue(e.getScrollAmount() + end.getValue());
			else
				end.setValue(end.getValue() - e.getScrollAmount());
		} // end mouseWheelMoved
	});

    gBc.gridx = 1;
    gBc.gridy = 7;
    gBc.gridwidth = 1;
    gBc.weightx = 0.0;
    gBc.insets = new Insets(0,5,15,0);
    this.lab_end = new JLabel("End: ");
    this.vars.add(this.lab_end, gBc);

    gBc.gridx = 2;
    gBc.gridy = 7;
    gBc.fill = GridBagConstraints.NONE;
    this.endVal = new JFormattedTextField(formatter);
    this.endVal.setValue(new Float(25.00f));
    this.endVal.setColumns(5);
    this.endVal.setHorizontalAlignment(JFormattedTextField.CENTER);
    this.vars.add(this.endVal, gBc);
    this.endVal.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
    this.endVal.getActionMap().put("check", new AbstractAction(){
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if (!endVal.isEditValid()) {
				endVal.selectAll();
				//need some way to tell people that text they entered was invalid
			} // end if
			else
				try {
					endVal.commitEdit();
					float Fend = Float.parseFloat(endVal.getText());
					end.setValue( (int) Fend * 100);
					theScene.Fend = (Fend);
					theScene.refreshCanvas();
				} // end try
				catch (java.text.ParseException exc) {}
		} // end actionPerformed
	});

    gBc.gridx = 0;
    gBc.gridy = 10;
    gBc.weightx = 1.0;
    gBc.fill = GridBagConstraints.BOTH;
    this.add(this.vars, gBc);

	// Disable all settings initially until fog is enabled
	setSettingsEnabled(false);

  } // end FogPanel Constructor

  /**
   * This class provides a ChangeListener for the color JSliders.
   */
  public class SliderListener implements ChangeListener
  {
	  public void stateChanged(ChangeEvent e) {
		  JSlider source = (JSlider) e.getSource();
		  int sVal = source.getValue();
		  if (source.getName().equals("red")) {
			  redVal.setValue(new Integer(sVal));
			  theScene.Fred = (sVal);
		  } // end if
		  else if (source.getName().equals("green")) {
			  greenVal.setValue(new Integer(sVal));
			  theScene.Fgreen = (sVal);
		  } // end if
		  else if (source.getName().equals("blue")) {
			  blueVal.setValue(new Integer(sVal));
			  theScene.Fblue = (sVal);
		  } // end if
		  else if (source.getName().equals("density")) {
			  double temp = (double) (sVal) / 100;
			  densityVal.setValue(new Double(temp));
			  theScene.Fdensity = (temp);
		  } // end if
		  else if (source.getName().equals("start")) {
			  if (sVal >= end.getValue()) {
				  float s = (float) end.getValue() / 100;
				  s -= .01;
				  startVal.setValue(new Float(s));
				  theScene.Fstart = (s);
				  s *= 100;
				  start.setValue( (int) s);
				  start.updateUI();
			  } // end if
			  else {
				  float start = (float) sVal / 100;
				  startVal.setValue(new Float(start));
				  theScene.Fstart = (start);
			  } // end else
		  } // end if
		  else if (source.getName().equals("end")) {
			  if (sVal <= start.getValue()) {
				  float tail = ( (float) start.getValue() / 100);
				  tail += .01;
				  endVal.setValue(new Float(tail));
				  theScene.Fend = (tail);
				  tail *= 100;
				  end.setValue( (int) tail);
				  end.updateUI();
			  } // end if
			  else {
				  float end = (float) sVal / 100;
				  endVal.setValue(new Float(end));
				  theScene.Fend = (end);
			  } // end else
		  } // end if
		  else if (source.getName().equals("alpha")) {
			  float a = (float) sVal / 100;
			  alphaVal.setValue(new Double(a));
			  theScene.Falpha = (a);
		  } // end if
		  if (clearColor) {
			  float r = (float) red.getValue() / 256;
			  float g = (float) green.getValue() / 256;
			  float b = (float) blue.getValue() / 256;
			  float alpha = 1.0f;
			  theScene.setClearColor(r, g, b, alpha);
		  } // end if
		  theScene.refreshCanvas();
	  } // end method stateChanged
  } // end class SliderListener

  /**
   * This method enables or disabled all of the GUI settings for the fog.
   */
  public void setSettingsEnabled(boolean enabled)
  {
		setClearColor.setEnabled(enabled);
		red.setEnabled(enabled);
		redVal.setEnabled(enabled);
		green.setEnabled(enabled);
		greenVal.setEnabled(enabled);
		blue.setEnabled(enabled);
		blueVal.setEnabled(enabled);
		alpha.setEnabled(enabled);
		alphaVal.setEnabled(enabled);
		linear.setEnabled(enabled);
		exp.setEnabled(enabled);
		exp2.setEnabled(enabled);
		dontCare.setEnabled(enabled);
		nicest.setEnabled(enabled);
		fastest.setEnabled(enabled);

		if(enabled) {
			if(linear.isSelected()) // only allow start and end to be enabled if linear is selected
	  			setStartEndEnabled(enabled);
	  		if(exp.isSelected() || exp2.isSelected()) // otherwise enable the density
				setDensityEnabled(enabled);
		} // end if
		else { // allow disabling
			setStartEndEnabled(enabled);
			setDensityEnabled(enabled);
		} // end else
  } // end method setSettingsEnabled

  /**
   * This method enables or disables the GUI settings for start and end.
   * This is required because these settings only affect GL_LINEAR fog.
   */
  public void setStartEndEnabled(boolean enabled)
  {
		start.setEnabled(enabled);
		startVal.setEnabled(enabled);
		end.setEnabled(enabled);
		endVal.setEnabled(enabled);
  } // end method setStartEndEnabled

  /**
   * This method enables or disables the GUI settings for density.
   * This is required because these settings only affect GL_EXP and GL_EXP2 fog.
   */
  public void setDensityEnabled(boolean enabled)
  {
		density.setEnabled(enabled);
		densityVal.setEnabled(enabled);
  } // end method setDensityEnabled

} // end class FogPanel
