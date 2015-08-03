/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 * 
 * Modified by Altug Uzunali (altug.uzunali@gmail.com) at 08/02/2012
 * 
 * Added two thumbs functionality
 * 
 */
package doubleslider;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.util.StringConverter;

import com.sun.javafx.Utils;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.css.CssMetaData;
import javafx.css.Styleable;

public class DoubleSlider extends Control {

	public DoubleSlider(){
		initialize();
	}
	
	private void initialize() {
        //Initialize the style class to be 'double-slider'.
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        
    }
    /**
     * The maximum value represented by this DoubleSlider. This must be a
     * value greater than {@link #minProperty() min}.
     */
    private DoubleProperty max;
    public final void setMax(double value) {
        maxProperty().set(value);
    }

    public final double getMax() {
        return max == null ? 100 : max.get();
    }

    public final DoubleProperty maxProperty() {
        if (max == null) {
            max = new DoublePropertyBase(100) {
                @Override protected void invalidated() {
                    if (get() < getMin()) {
                        setMin(get());
                    }
                    adjustValues();
                }

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "max";
                }
            };
        }
        return max;
    }
    /**
     * The minimum value represented by this DoubleSlider. This must be a
     * value less than {@link #maxProperty() max}.
     */
    private DoubleProperty min;
    public final void setMin(double value) {
        minProperty().set(value);
    }

    public final double getMin() {
        return min == null ? 0 : min.get();
    }

    public final DoubleProperty minProperty() {
        if (min == null) {
            min = new DoublePropertyBase(0) {
                @Override protected void invalidated() {
                    if (get() > getMax()) {
                        setMax(get());
                    }
                    adjustValues();
                }

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "min";
                }
            };
        }
        return min;
    }
    /**
     * Adjusts {@link #valueProperty1() value1} and {@link #valueProperty2() value2}
     * to match <code>newValue</code>. The <code>value</code>is the actual amount between the 
     * {@link #minProperty() min} and {@link #maxProperty() max}. This function
     * also takes into account {@link #snapToTicksProperty() snapToTicks}, which
     * is the main difference between adjustValue and setValue. It also ensures 
     * that the value is some valid number between min and max.
     *
     * @expert This function is intended to be used by experts, primarily
     *         by those implementing new Skins or Behaviors. It is not common
     *         for developers or designers to access this function directly.
     */
    public void adjustValue(double newValue) {
        // figure out the "value" associated with the specified position
        final double _min = getMin();
        final double _max = getMax();
        if (_max <= _min) return;
        newValue = newValue < _min ? _min : newValue;
        newValue = newValue > _max ? _max : newValue;
        // snap value to ticks doesn't work since the keyboard bindings have been removed
        setValue1(snapValueToTicks(newValue));
        setValue2(snapValueToTicks(newValue));
        adjustValues();
    }

    /**
     * The current value1 represented by this DoubleSlider. This value must
     * always be between {@link #minProperty() min} and {@link #valueProperty2() value2},
     * inclusive. If it is ever out of bounds either due to {@code min} or
     * {@code value2} changing or due to itself being changed, then it will
     * be clamped to always remain valid.
     */
    private DoubleProperty value1;
    public final void setValue1(double value) {
        if (!value1Property().isBound()) value1Property().set(value);
    }

    public final double getValue1() {
        return value1 == null ? 0 : value1.get();
    }

    public final DoubleProperty value1Property() {
        if (value1 == null) {
            value1 = new DoublePropertyBase(0) {
                @Override protected void invalidated() {

                }

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "value1";
                }
            };
        }
        return value1;
    }
    /**
     * The current value2 represented by this DoubleSlider. This value must
     * always be between {@link #valueProperty1() value1} and {@link #maxProperty() max},
     * inclusive. If it is ever out of bounds either due to {@code max} or
     * {@code value1} changing or due to itself being changed, then it will
     * be clamped to always remain valid.
     */
    private DoubleProperty value2;
    public final void setValue2(double value) {
        if (!value2Property().isBound()) value2Property().set(value);
    }

    public final double getValue2() {
        return value2 == null ? 0 : value2.get();
    }

    public final DoubleProperty value2Property() {
        if (value2 == null) {
            value2 = new DoublePropertyBase(0) {
                @Override protected void invalidated() {

                }

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "value2";
                }
            };
        }
        return value2;
    }
    /**
     * The orientation of the {@code DoubleSlider} can either be horizontal
     * or vertical.
     */
    private ObjectProperty<Orientation> orientation;
    public final void setOrientation(Orientation value) {
        orientationProperty().set(value);
    }

    public final Orientation getOrientation() {
        return orientation == null ? Orientation.HORIZONTAL : orientation.get();
    }

    public final ObjectProperty<Orientation> orientationProperty() {
        if (orientation == null) {
            orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
                @Override protected void invalidated() {
                    /*[DEPRECATED]
                    impl_pseudoClassStateChanged(PSEUDO_CLASS_VERTICAL);
                    impl_pseudoClassStateChanged(PSEUDO_CLASS_HORIZONTAL);
                    */
                }
                
                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "orientation";
                }

                @Override
                public CssMetaData<? extends Styleable, Orientation> getCssMetaData() {
                    return StyleableProperties.ORIENTATION;
                }
            };
        }
        return orientation;
    }
    
    
    /**
     * Indicates that the labels for tick marks should be shown. Typically a
     * {@link Skin} implementation will only show labels if
     * {@link #showTickMarksProperty() showTickMarks} is also true.
     */
    private BooleanProperty showTickLabels;
    public final void setShowTickLabels(boolean value) {
        showTickLabelsProperty().set(value);
    }

    public final boolean isShowTickLabels() {
        return showTickLabels == null ? false : showTickLabels.get();
    }

    public final BooleanProperty showTickLabelsProperty() {
        if (showTickLabels == null) {
            showTickLabels = new StyleableBooleanProperty(false) {

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "showTickLabels";
                }

                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return StyleableProperties.SHOW_TICK_LABELS;
                }
            };
        }
        return showTickLabels;
    }
    /**
     * Specifies whether the {@link Skin} implementation should show tick marks.
     */
    private BooleanProperty showTickMarks;
    public final void setShowTickMarks(boolean value) {
        showTickMarksProperty().set(value);
    }

    public final boolean isShowTickMarks() {
        return showTickMarks == null ? false : showTickMarks.get();
    }

    public final BooleanProperty showTickMarksProperty() {
        if (showTickMarks == null) {
            showTickMarks = new StyleableBooleanProperty(false) {

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "showTickMarks";
                }

                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return StyleableProperties.SHOW_TICK_MARKS;
                }
            };
        }
        return showTickMarks;
    }
    /**
     * The unit distance between major tick marks. For example, if
     * the {@link #minProperty() min} is 0 and the {@link #maxProperty() max} is 100 and the
     * {@link #majorTickUnitProperty() majorTickUnit} is 25, then there would be 5 tick marks: one at
     * position 0, one at position 25, one at position 50, one at position
     * 75, and a final one at position 100.
     * <p>
     * This value should be positive and should be a value less than the
     * span. Out of range values are essentially the same as disabling
     * tick marks.
     */
    private DoubleProperty majorTickUnit;
    public final void setMajorTickUnit(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
        }
        majorTickUnitProperty().set(value);
    }

    public final double getMajorTickUnit() {
        return majorTickUnit == null ? 25 : majorTickUnit.get();
    }

    public final DoubleProperty majorTickUnitProperty() {
        if (majorTickUnit == null) {
            majorTickUnit = new StyleableDoubleProperty(25) {
                @Override
                public void invalidated() {
                    if (get() <= 0) {
                        throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
                    }
                }
                
                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "majorTickUnit";
                }

                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return StyleableProperties.MAJOR_TICK_UNIT;
                }
            };
        }
        return majorTickUnit;
    }
    /**
     * The number of minor ticks to place between any two major ticks. This
     * number should be positive or zero. Out of range values will disable
     * disable minor ticks, as will a value of zero.
     */
    private IntegerProperty minorTickCount;
    public final void setMinorTickCount(int value) {
        minorTickCountProperty().set(value);
    }

    public final int getMinorTickCount() {
        return minorTickCount == null ? 3 : minorTickCount.get();
    }

    public final IntegerProperty minorTickCountProperty() {
        if (minorTickCount == null) {
            minorTickCount = new StyleableIntegerProperty(3) {

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "minorTickCount";
                }

                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return StyleableProperties.MINOR_TICK_COUNT;
                }
            };
        }
        return minorTickCount;
    }
    /**
     * Indicates whether the {@link #valueProperty() value} of the {@code DoubleSlider} should always
     * be aligned with the tick marks. This is honored even if the tick marks
     * are not shown.
     * 
     * However it is useless since the keyborad binding have been removed (Altug Uzunali)
     */
    private BooleanProperty snapToTicks;
    public final void setSnapToTicks(boolean value) {
        snapToTicksProperty().set(value);
    }

    public final boolean isSnapToTicks() {
        return snapToTicks == null ? false : snapToTicks.get();
    }

    public final BooleanProperty snapToTicksProperty() {
        if (snapToTicks == null) {
            snapToTicks = new StyleableBooleanProperty(false) {
                
                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "snapToTicks";
                }

                @Override
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return StyleableProperties.SNAP_TO_TICKS;
                }
            };
        }
        return snapToTicks;
    }
    /**
     * A function for formatting the label for a major tick. The number
     * representing the major tick will be passed to the function. If this
     * function is not specified, then a default function will be used by
     * the {@link Skin} implementation.
     */
    private ObjectProperty<StringConverter<Double>> labelFormatter;

    public final void setLabelFormatter(StringConverter<Double> value) {
        labelFormatterProperty().set(value);
    }

    public final StringConverter<Double> getLabelFormatter() {
        return labelFormatter == null ? null : labelFormatter.get();
    }

    public final ObjectProperty<StringConverter<Double>> labelFormatterProperty() {
        if (labelFormatter == null) {
            labelFormatter = new SimpleObjectProperty<StringConverter<Double>>(this, "labelFormatter");
        }
        return labelFormatter;
    }
    /**
     * The amount by which to adjust the DoubleSlider if the track of the DoubleSlider is
     * clicked. This is used when manipulating the DoubleSlider position using keys. If
     * {@link #snapToTicksProperty() snapToTicks} is true then the nearest tick mark to the adjusted
     * value will be used.
     */
    private DoubleProperty blockIncrement;
    public final void setBlockIncrement(double value) {
        blockIncrementProperty().set(value);
    }

    public final double getBlockIncrement() {
        return blockIncrement == null ? 10 : blockIncrement.get();
    }

    public final DoubleProperty blockIncrementProperty() {
        if (blockIncrement == null) {
            blockIncrement = new StyleableDoubleProperty(10) {

                @Override
                public Object getBean() {
                    return DoubleSlider.this;
                }

                @Override
                public String getName() {
                    return "blockIncrement";
                }

                @Override
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return StyleableProperties.BLOCK_INCREMENT;
                }
            };
        }
        return blockIncrement;
    }
    /**
     * Ensures that value1 is always somewhere between the min and value2, and that
     * value2 is always somewhere between the value1 and max, and that 
     * if snapToTicks is set then the values will always be set to align with a tick mark.
     */
    private void adjustValues() {
        if ((getValue1() < getMin() || getValue1() > getValue2()) /* &&  !isReadOnly(value)*/)
             setValue1(Utils.clamp(getMin(), getValue1(), getValue2()));
        if ((getValue2() < getValue1() || getValue2() > getMax()) /* &&  !isReadOnly(value)*/)
            setValue2(Utils.clamp(getValue1(), getValue2(), getMax()));
    }
    /**
     * Utility function which, given the specified value, will position it
     * either aligned with a tick, or simply clamp between min & max value,
     * depending on whether snapToTicks is set.
     *
     * @expert This function is intended to be used by experts, primarily
     *         by those implementing new Skins or Behaviors. It is not common
     *         for developers or designers to access this function directly.
     */
    private double snapValueToTicks(double val) {
        double v = val;
        if (isSnapToTicks()) {
            double tickSpacing = 0;
            // compute the nearest tick to this value
            if (getMinorTickCount() != 0) {
                tickSpacing = getMajorTickUnit() / (Math.max(getMinorTickCount(),0)+1);
            } else {
                tickSpacing = getMajorTickUnit();
            }
            int prevTick = (int)((v - getMin())/ tickSpacing);
            double prevTickValue = (prevTick) * tickSpacing + getMin();
            double nextTickValue = (prevTick + 1) * tickSpacing + getMin();
            v = Utils.nearest(prevTickValue, v, nextTickValue);
        }
        return Utils.clamp(getMin(), v, getMax());
    }
    
    /**
     * When true, indicates the current value of this Slider is changing.
     * It provides notification that the value is changing. Once the value is
     * computed, it is reset back to false.
     */
    private BooleanProperty valueChanging;

    public final void setValueChanging(boolean value) {
        valueChangingProperty().set(value);
    }

    public final boolean isValueChanging() {
        return valueChanging == null ? false : valueChanging.get();
    }

    public final BooleanProperty valueChangingProperty() {
        if (valueChanging == null) {
            valueChanging = new SimpleBooleanProperty(this, "valueChanging", false);
        }
        return valueChanging;
    }
    
    /***************************************************************************
     *                                                                         *
     *                         Stylesheet Handling                             *
     *                                                                         *
     **************************************************************************/

    private static final String DEFAULT_STYLE_CLASS = "double-slider";
    private static final String PSEUDO_CLASS_VERTICAL = "vertical";
    private static final String PSEUDO_CLASS_HORIZONTAL = "horizontal";

    private static class StyleableProperties {
        private static final CssMetaData <DoubleSlider,Number> BLOCK_INCREMENT =
            new CssMetaData <DoubleSlider,Number>("-fx-block-increment",
                SizeConverter.getInstance(), 10.0) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.blockIncrement == null || !n.blockIncrement.isBound();
            }

            /*@Override
            public WritableValue<Number> getWritableValue(DoubleSlider n) {
                return n.blockIncrementProperty();
            }*/
            
            @Override
            public StyleableProperty<Number> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Number>)n.blockIncrement;
            }
        };
    	
       
        private static final CssMetaData<DoubleSlider,Boolean> SHOW_TICK_LABELS =
            new CssMetaData<DoubleSlider,Boolean>("-fx-show-tick-labels",
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.showTickLabels == null || !n.showTickLabels.isBound();
            }
            /*
            @Override
            public WritableValue<Boolean> getWritableValue(DoubleSlider n) {
                return n.showTickLabelsProperty();
            }*/

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Boolean> )n.showTickLabels;
            }
        };
                    
        private static final CssMetaData<DoubleSlider,Boolean> SHOW_TICK_MARKS =
            new CssMetaData<DoubleSlider,Boolean>("-fx-show-tick-marks",
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.showTickMarks == null || !n.showTickMarks.isBound();
            }

            /*@Override
            public WritableValue<Boolean> getWritableValue(DoubleSlider n) {
                return n.showTickMarksProperty();
            }*/

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Boolean>)n.showTickMarks;
            }
        };
        
        private static final CssMetaData<DoubleSlider,Boolean> SNAP_TO_TICKS =
            new CssMetaData<DoubleSlider,Boolean>("-fx-snap-to-ticks",
                BooleanConverter.getInstance(), Boolean.FALSE) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.snapToTicks == null || !n.snapToTicks.isBound();
            }
            /*
            @Override
            public WritableValue<Boolean> getWritableValue(DoubleSlider n) {
                return n.snapToTicksProperty();
            }
            */
            @Override
            public StyleableProperty<Boolean> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Boolean>)n.snapToTicks;
            }
        };
        
        private static final CssMetaData<DoubleSlider,Number> MAJOR_TICK_UNIT =
            new CssMetaData<DoubleSlider,Number>("-fx-major-tick-unit",
                SizeConverter.getInstance(), 25.0) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.majorTickUnit == null || !n.majorTickUnit.isBound();
            }
            /*
            @Override
            public WritableValue<Number> getWritableValue(DoubleSlider n) {
                return n.majorTickUnitProperty();
            }
            */

            @Override
            public StyleableProperty<Number> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Number>)n.majorTickUnit;
            }
        };
        
        private static final CssMetaData<DoubleSlider,Number> MINOR_TICK_COUNT =
            new CssMetaData<DoubleSlider,Number>("-fx-minor-tick-count",
                SizeConverter.getInstance(), 3.0) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.minorTickCount == null || !n.minorTickCount.isBound();
            }

            /*@Override
            public WritableValue<Number> getWritableValue(DoubleSlider n) {
                return n.minorTickCountProperty();
            }*/

            @Override
            public StyleableProperty<Number> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Number>)n.minorTickCount;
            }
        };
        
        private static final CssMetaData<DoubleSlider,Orientation> ORIENTATION =
            new CssMetaData<DoubleSlider,Orientation>("-fx-orientation",
                new EnumConverter<>(Orientation.class), 
                Orientation.HORIZONTAL) {

            @Override
            public boolean isSettable(DoubleSlider n) {
                return n.orientation == null || !n.orientation.isBound();
            }

            /*@Override
            public WritableValue<Orientation> getWritableValue(DoubleSlider n) {
                return n.orientationProperty();
            }*/

            @Override
            public StyleableProperty<Orientation> getStyleableProperty(DoubleSlider n) {
                return (StyleableProperty<Orientation> )n.orientation;
            }
        };

        /*[DEPRECATED]
        private static final List<StyleableProperty> STYLEABLES;
        static {
            final List<StyleableProperty> styleables = 
                new ArrayList<StyleableProperty>(Control.impl_CSS_STYLEABLES());
            Collections.addAll(styleables,
                BLOCK_INCREMENT,
                SHOW_TICK_LABELS,
                SHOW_TICK_MARKS,
                SNAP_TO_TICKS,
                MAJOR_TICK_UNIT,
                MINOR_TICK_COUNT,
                ORIENTATION
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        */
    }

}
