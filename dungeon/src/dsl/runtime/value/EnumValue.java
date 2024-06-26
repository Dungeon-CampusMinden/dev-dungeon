package dsl.runtime.value;

import dsl.semanticanalysis.symbol.Symbol;
import dsl.semanticanalysis.typesystem.typebuilding.type.EnumType;

/**
 * This class represents a {@link Value} containing an enum-variant (stored as the {@link Symbol}
 * representing the variant).
 */
public class EnumValue extends Value {
  /**
   * Constructor.
   *
   * @param enumType The {@link EnumType} of this Value
   * @param enumVariantSymbol The {@link Symbol} representing the variant
   */
  public EnumValue(EnumType enumType, Symbol enumVariantSymbol) {
    super(enumType, enumVariantSymbol);
  }

  /**
   * @return the variant represented by this {@link EnumValue}
   */
  public Symbol getEnumVariantSymbol() {
    return (Symbol) this.getInternalValue();
  }
}
