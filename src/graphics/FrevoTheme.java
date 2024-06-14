package graphics;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.OceanTheme;

public class FrevoTheme extends OceanTheme {

	public String getName() { return "Frevo"; }
	
	private static ColorUIResource primary1;
	private static ColorUIResource primary2;
	private static ColorUIResource primary3;
	private static ColorUIResource secondary1;
	private static ColorUIResource secondary2;
	private static ColorUIResource secondary3;
	ColorUIResource black, white; //these get inverted while darkmode is active
	private boolean darkMode;
	public Font blueprintFont;
	
	
	public FrevoTheme() {
		super();
		setDarkMode(false);	//init base colors
		/*	try {
			blueprintFont = Font.createFont(Font.TRUETYPE_FONT, new File("./Drawable/blueprintextended.ttf"));
			blueprintFont = blueprintFont.deriveFont(Font.BOLD,12);
			System.out.println(blueprintFont.getFontName());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	 /**
	  * Activate or deactivate darkMode
	  */
	public void setDarkMode(boolean darkMode)
	{
		this.darkMode = darkMode;
		if(darkMode)
		{
			//swap black and white
			black = new ColorUIResource(0xFFFFFF);
			white = new ColorUIResource(0x000000);
			//darker base colors or brighten them, if they are used for text
			primary1 = new ColorUIResource(super.getPrimary1().darker());
			primary2 = new ColorUIResource(super.getPrimary2().darker());
			primary3 = new ColorUIResource(super.getPrimary3().darker());
			secondary1 = 	new ColorUIResource(super.getSecondary1().brighter());
			secondary2 = new ColorUIResource(super.getSecondary2().darker());
			secondary3 = new ColorUIResource(super.getSecondary3().darker().darker());
		}
		else
		{
			//set values back to default
			black = super.getBlack();
			white = super.getWhite();
			primary1 = super.getPrimary1();
			primary2 = super.getPrimary2();
			primary3 = super.getPrimary3();
			secondary1 = super.getSecondary1();
			secondary2 = super.getSecondary2();
			secondary3 = super.getSecondary3();
		}
	}
	
	@Override
	protected ColorUIResource getPrimary1() {
        return primary1;
    }
	
	@Override
	protected ColorUIResource getPrimary2() {
        return primary2;
    }
	
	@Override
	protected ColorUIResource getPrimary3() {
        return primary3;
    }
	
	@Override
	protected ColorUIResource getSecondary1() {
        return secondary1;
    }
	@Override
	protected ColorUIResource getSecondary2() {
        return secondary2;
    }
	
	@Override
	protected ColorUIResource getSecondary3() {
        return secondary3;
    }
	
	/**
	 * The black color, becomes white in darkMode
	 */
	@Override
	protected ColorUIResource getBlack() {
        return black;
    }
	
	/**
	 * The white color, becomes black in darkMode
	 */
	@Override
	protected ColorUIResource getWhite() {
		return white;
	}
	
	public boolean getDarkMode()
	{
		return darkMode;
	}
	
	/**
     * Returns the desktop color.
     * This returns the white color
     * @return the desktop color
     */
	@Override
    public ColorUIResource getDesktopColor() {
        return getWhite();
    }
	
	/**
	 * The text color for controls
	 * @return returns black
	 */
	@Override
    public ColorUIResource getControlTextColor() {
        return black;
    }
	
	public ColorUIResource getMenuForeground() { return  getBlack(); }
	
	/**
     * Add this theme's custom entries to the defaults table.
     * Inherits all entries from OceanTheme, but adapts some for darkMode
     * @param table the defaults table
     */
	@Override
	public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);
		if(darkMode)
		{
			//does the same thing as addCustomEntries from OceanTheme, but slightly darker
			//does not change any not color related or already dependent on primary colors/white/black entries
	        java.util.List<?> buttonGradient = Arrays.asList(
	                 new Object[] {Float.valueOf(.3f), Float.valueOf(0f),
	                 new ColorUIResource(new Color(0xDDE8F3).darker()), getWhite(), getSecondary2() });
	        Color _aaaaaa = new ColorUIResource(0xAAAAAA);
	        Color a0b1c2 = new ColorUIResource(0xA0B1C2);
	        java.util.List<?> sliderGradient = Arrays.asList(new Object[] {
	            Float.valueOf(.3f), Float.valueOf(.2f),
	            a0b1c2, getWhite(), getSecondary2() });
	
	        Object[] defaults = new Object[] {
	            "Button.gradient", buttonGradient,
	            "Button.disabledToolBarBorderBackground", _aaaaaa,
	
	            "CheckBox.gradient", buttonGradient,
	
	            "CheckBoxMenuItem.gradient", buttonGradient,
	
	            "RadioButton.gradient", buttonGradient,
	
	            "RadioButtonMenuItem.gradient", buttonGradient,
	
	            "ScrollBar.gradient", buttonGradient,
	
	            "Slider.altTrackColor", new ColorUIResource(new Color(0xD2E2EF).darker()),
	            "Slider.gradient", sliderGradient,
	            "Slider.focusGradient", sliderGradient,
	            
	            "MenuBar.gradient", Arrays.asList(new Object[] {
	                    Float.valueOf(1f), Float.valueOf(0f),
	                    getWhite(), new ColorUIResource(0x333333),
	                    new ColorUIResource(0x333333) }),
	
	            "SplitPane.dividerFocusColor", a0b1c2,
	
	            "TabbedPane.contentAreaColor", a0b1c2,
	            "TabbedPane.selected", a0b1c2,
	            "TabbedPane.tabAreaBackground", new ColorUIResource(0xA0A0A0),
	            "TabbedPane.unselectedBackground", getSecondary3(),
	            
	            "Table.gridColor", getSecondary1(),
	            "TableHeader.focusCellBackground", a0b1c2,	            
	
	            "ToggleButton.gradient", buttonGradient,
	
	            "ToolBar.borderColor", _aaaaaa,
	            "Table.dropLineShortColor", _aaaaaa,
	
	            "Table.dropCellBackground", _aaaaaa,
	            "Tree.dropCellBackground", _aaaaaa,
	            "List.dropCellBackground", _aaaaaa,
	            "List.dropLineColor", getPrimary1(),
	            
	        };
	        table.putDefaults(defaults);
		}
	}
	
	//public FontUIResource getWindowTitleFont() { return fWindowTitleFont;}
	//	public FontUIResource getControlTextFont() { return new FontUIResource(blueprintFont);}
    //public FontUIResource getSystemTextFont() { return fWindowTitleFont;}
    //public FontUIResource getUserTextFont() { return fWindowTitleFont;}
    //public FontUIResource getMenuTextFont() { return fWindowTitleFont;}
	
	//private final FontUIResource fWindowTitleFont = new FontUIResource(blueprintFont);
}
