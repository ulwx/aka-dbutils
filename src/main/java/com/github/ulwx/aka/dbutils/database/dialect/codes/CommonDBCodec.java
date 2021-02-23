
package com.github.ulwx.aka.dbutils.database.dialect.codes;

public class CommonDBCodec extends AbstractCharacterCodec {


	/**
	 * {@inheritDoc}
	 * 
	 * Encodes ' to ''
     *
	 * Encodes ' to ''
     *
     * @param immune
     */
	public String encodeCharacter( char[] immune, Character c ) {
		if ( c.charValue() == '\'' )
        	return "\'\'";
        return ""+c;
	}
	


	/**
	 * {@inheritDoc}
	 *
	 * Returns the decoded version of the character starting at index, or
	 * null if no decoding is possible.
	 *
	 * Formats all are legal
	 *   '' decodes to '
	 */
	public Character decodeCharacter( PushbackSequence<Character> input ) {
		input.mark();
		Character first = input.next();
		if ( first == null ) {
			input.reset();
			return null;
		}

		// if this is not an encoded character, return null
		if ( first.charValue() != '\'' ) {
			input.reset();
			return null;
		}

		Character second = input.next();
		if ( second == null ) {
			input.reset();
			return null;
		}
		
		// if this is not an encoded character, return null
		if ( second.charValue() != '\'' ) {
			input.reset();
			return null;
		}
		return( Character.valueOf( '\'' ) );
	}

}