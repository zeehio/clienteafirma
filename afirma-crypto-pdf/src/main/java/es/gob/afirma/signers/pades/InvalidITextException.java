/* Copyright (C) 2011 [Gobierno de Espana]
 * This file is part of "Cliente @Firma".
 * "Cliente @Firma" is free software; you can redistribute it and/or modify it under the terms of:
 *   - the GNU General Public License as published by the Free Software Foundation;
 *     either version 2 of the License, or (at your option) any later version.
 *   - or The European Software License; either version 1.1 or (at your option) any later version.
 * Date: 11/01/11
 * You may contact the copyright holder at: soporte.afirma5@mpt.es
 */

package es.gob.afirma.signers.pades;

import es.gob.afirma.core.InvalidLibraryException;

/** Indica que hay un iText inv&aacute;lido en el CLASSPATH o en el BOOTCLASSPATH, a menudo
 * porque se ha instalado el JAR inapropiadamente como extensi&oacute;n del JRE.
 * @author Tom&aacute;s Garc&iacute;a-Mer&aacute;s */
public final class InvalidITextException extends InvalidLibraryException {

	private static final long serialVersionUID = -322997692480101275L;

	private final String exp;
	private final String fnd;

	/** Crea una instancia de la excepci&oacute;n.
	 * @param expected Versi&oacute;n esperada de iText
	 * @param found Versi&oacute;n encontrada (actual) de iText */
	InvalidITextException(final String expected, final String found) {
		super("Se necesitaba iText version " + expected + ", pero se encontro la version " + found); //$NON-NLS-1$ //$NON-NLS-2$
		this.exp = expected;
		this.fnd = found;
	}

	/** Obtiene la versi&oacute;n esperada de iText.
	 * @return Versi&oacute;n esperada de iText */
	public String getExpectedVersion() {
		return this.exp;
	}

	/** Obtiene la versi&oacute;n encontrada (actual) de iText.
	 * @return Versi&oacute;n encontrada (actual) de iText */
	public String getFoundVersion() {
		return this.fnd;
	}

}
