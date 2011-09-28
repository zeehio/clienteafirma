/*
 * Este fichero forma parte del Cliente @firma.
 * El Cliente @firma es un applet de libre distribucion cuyo codigo fuente puede ser consultado
 * y descargado desde www.ctt.map.es.
 * Copyright 2009,2010 Ministerio de la Presidencia, Gobierno de Espana
 * Este fichero se distribuye bajo licencia GPL version 3 segun las
 * condiciones que figuran en el fichero 'licence' que se acompana.  Si se   distribuyera este
 * fichero individualmente, deben incluirse aqui las condiciones expresadas alli.
 */
package es.gob.afirma.ui.wizardmultifirmacofirma;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Caret;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.common.AOKeyStoreManager;
import es.gob.afirma.keystores.common.KeyStoreConfiguration;
import es.gob.afirma.ui.utils.ConfigureCaret;
import es.gob.afirma.ui.utils.GeneralConfig;
import es.gob.afirma.ui.utils.HelpUtils;
import es.gob.afirma.ui.utils.JAccessibilityDialogWizard;
import es.gob.afirma.ui.utils.Messages;
import es.gob.afirma.ui.utils.MultisignUtils;
import es.gob.afirma.ui.utils.SelectionDialog;
import es.gob.afirma.ui.utils.Utils;
import es.gob.afirma.ui.wizardUtils.BotoneraInferior;
import es.gob.afirma.ui.wizardUtils.CabeceraAsistente;
import es.gob.afirma.ui.wizardUtils.JDialogWizard;
import es.gob.afirma.util.signers.AOSignerFactory;

/**
 * Clase que muestra el contenido principal de multifirmas - cofirma.
 */
public class PanelCofirma extends JAccessibilityDialogWizard {

	private static final long serialVersionUID = 1L;

	static Logger logger = Logger.getLogger(PanelCofirma.class.getName());

	@Override
	public int getMinimumRelation(){
		return 9;
	}
	
	/**
	 * Configuracion del KeyStore
	 */
	private KeyStoreConfiguration kssc = null;

	/**
	 * Guarda todas las ventanas del asistente para poder controlar la botonera
	 * @param ventanas	Listado con todas las paginas del asistente
	 */
	public void setVentanas(List<JDialogWizard> ventanas) {
		Botonera botonera = new Botonera(ventanas, 1);
		getContentPane().add(botonera, BorderLayout.PAGE_END);
	}

	public PanelCofirma(KeyStoreConfiguration kssc) {
		this.kssc = kssc;
		initComponents();
	}

	// Campo donde se guarda el nombre del fichero de datos
	private JTextField campoDatos = new JTextField();
	// Campo donde se guarda el nombre del fichero de firma
	private JTextField campoFirma = new JTextField();

	/**
	 * Inicializacion de componentes
	 */
	private void initComponents() {
		// Titulo de la ventana
    	setTitulo(Messages.getString("Wizard.multifirma.simple.cofirma.titulo"));
		
		// Panel con la cabecera
		CabeceraAsistente panelSuperior = new CabeceraAsistente("Wizard.multifirma.simple.ventana1.titulo", "Wizard.multifirma.simple.ventana1.titulo.descripcion", null, true);
		Utils.setContrastColor(panelSuperior);
		Utils.setFontBold(panelSuperior);
		getContentPane().add(panelSuperior, BorderLayout.NORTH);

		// Panel central
		JPanel panelCentral = new JPanel();
		panelCentral.setMinimumSize(new Dimension(603, 289));
		panelCentral.setLayout(new GridBagLayout());

		// Configuramos el layout
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(20, 20, 0, 20);
        c.gridwidth = 2;
		c.weightx = 1.0;
		c.gridx = 0;
		
		// Etiqueta "Fichero de datos:"
		JLabel etiquetaDatos = new JLabel();
		etiquetaDatos.setText(Messages.getString("Wizard.multifirma.simple.ventana1.fichero.datos"));
		Utils.setContrastColor(etiquetaDatos);
		Utils.setFontBold(etiquetaDatos);
		panelCentral.add(etiquetaDatos, c);

		c.insets = new Insets(0, 20, 0, 0);
		c.gridwidth = 1;
		c.gridy	= 1;
		
		// Caja de texto donde se guarda el nombre del archivo de datos
		campoDatos.setToolTipText(Messages.getString("Wizard.multifirma.simple.ventana1.fichero.datos.description"));
		campoDatos.getAccessibleContext().setAccessibleName(etiquetaDatos.getText() + " " + campoDatos.getToolTipText() + " " + "ALT + F.");
		campoDatos.getAccessibleContext().setAccessibleDescription(campoDatos.getToolTipText());
	     
		 if (GeneralConfig.isBigCaret()) {
			Caret caret = new ConfigureCaret();
			campoDatos.setCaret(caret);
		}
		Utils.remarcar(campoDatos);
	    Utils.setContrastColor(campoDatos);
		Utils.setFontBold(campoDatos);
		panelCentral.add(campoDatos, c);
		
		//Relaci�n entre etiqueta y campo de texto
		etiquetaDatos.setLabelFor(campoDatos);
		//Asignaci�n de mnem�nico
		etiquetaDatos.setDisplayedMnemonic(KeyEvent.VK_F);

		c.insets = new Insets(0, 10, 0, 20);
		c.weightx = 0.0;
		c.gridx = 1;
		
		// Boton examinar (fichero datos)
		JButton	examinarDatos = new JButton();
		examinarDatos.setMnemonic(KeyEvent.VK_E);
		examinarDatos.setText(Messages.getString("PrincipalGUI.Examinar"));
		examinarDatos.setToolTipText(Messages.getString("PrincipalGUI.Examinar.description"));
		examinarDatos.getAccessibleContext().setAccessibleName(examinarDatos.getText() + " " + examinarDatos.getToolTipText());
		examinarDatos.getAccessibleContext().setAccessibleDescription(examinarDatos.getToolTipText());
		examinarDatos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				examinarDatosActionPerformed();
			}
		});
		Utils.remarcar(examinarDatos);
        Utils.setContrastColor(examinarDatos);
		Utils.setFontBold(examinarDatos);
		panelCentral.add(examinarDatos, c);

		c.insets = new Insets(20, 20, 0, 20);
		c.gridwidth = 2;
		c.gridy	= 2;
		c.gridx = 0;
		
		// Etiqueta "Fichero de firma:"
		JLabel etiquetaFirma = new JLabel();
		etiquetaFirma.setText(Messages.getString("Wizard.multifirma.simple.ventana1.fichero.firma"));
		Utils.setContrastColor(etiquetaFirma);
		Utils.setFontBold(etiquetaFirma);
		panelCentral.add(etiquetaFirma, c);

		c.insets = new Insets(0, 20, 0, 0);
		c.gridwidth = 1;
		c.gridy	= 3;
		
		// Caja de texto donde se guarda el nombre del archivo de la firma
		campoFirma.setToolTipText(Messages.getString("Wizard.multifirma.simple.ventana1.fichero.firma.description")); // NOI18N
		campoFirma.getAccessibleContext().setAccessibleName(etiquetaFirma.getText() + " " + campoFirma.getToolTipText() + " " + "ALT + I.");
		campoFirma.getAccessibleContext().setAccessibleDescription(campoFirma.getToolTipText());
		 if (GeneralConfig.isBigCaret()) {
			Caret caret = new ConfigureCaret();
			campoFirma.setCaret(caret);
		}
		Utils.remarcar(campoFirma);
	    Utils.setContrastColor(campoFirma);
		Utils.setFontBold(campoFirma);
		panelCentral.add(campoFirma, c);
		
		//Relaci�n entre etiqueta y campo de texto
		etiquetaFirma.setLabelFor(campoFirma);
		//Asignaci�n de mnem�nico
		etiquetaFirma.setDisplayedMnemonic(KeyEvent.VK_I);

		c.insets = new Insets(0, 10, 0, 20);
		c.gridx = 1;
		
		// Boton examinar (fichero firma)
		JButton examinarFirma = new JButton();
		examinarFirma.setMnemonic(KeyEvent.VK_X); //mnem�nico diferente al bot�n "Examinar" anterior
		examinarFirma.setText(Messages.getString("PrincipalGUI.Examinar"));
		examinarFirma.setToolTipText(Messages.getString("PrincipalGUI.Examinar.description"));
		examinarFirma.getAccessibleContext().setAccessibleName(examinarFirma.getText() + " " + examinarFirma.getToolTipText());
		examinarFirma.getAccessibleContext().setAccessibleDescription(examinarFirma.getToolTipText());
		examinarFirma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				examinarFirmaActionPerformed();
			}
		});
		Utils.remarcar(examinarFirma);
        Utils.setContrastColor(examinarFirma);
		Utils.setFontBold(examinarFirma);
		panelCentral.add(examinarFirma, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(20, 20, 0, 20);
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 0;
		c.gridy = 4;
		
		// Panel introducido para poder mantener la linea superior correcta
		Panel panelVacio = new Panel();
		panelCentral.add(panelVacio, c);

		getContentPane().add(panelCentral, BorderLayout.CENTER);

		// Accesos rapidos al menu de ayuda
		HelpUtils.enableHelpKey(campoDatos, "multifirma.wizard.ficherodatos");
		HelpUtils.enableHelpKey(campoFirma, "multifirma.wizard.ficherofirma");
	}

	/**
	 * Examina si se ha seleccionado un archivo correcto y guarda el nombre en su caja
	 */
	private void examinarDatosActionPerformed() {
		File selectedFile = new SelectionDialog().showFileOpenDialog(this, Messages.getString("PrincipalGUI.chooser.title"));
		if (selectedFile != null) {
			campoDatos.setText(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Examina si el archivo seleccionado es un archivo de firma y guarda el nombre en su caja
	 */
	private void examinarFirmaActionPerformed() {
		File selectedFile = new SelectionDialog().showFileOpenDialog(this, Messages.getString("Wizard.multifirma.simple.chooserFirm.tittle"));
		if (selectedFile != null) {
			campoFirma.setText(selectedFile.getAbsolutePath());
		}  
	}

	/**
	 * Botonera con funciones para la pagina panel de multifirma - cofirma
	 */
	private class Botonera extends BotoneraInferior {

		private static final long serialVersionUID = 1L;

		public Botonera(List<JDialogWizard> ventanas, Integer posicion) {
			super(ventanas, posicion);
		}

		@Override
		protected void siguienteActionPerformed(JButton anterior,
				JButton siguiente, JButton finalizar) {

			Boolean continuar = true;
			continuar = cofirmaFichero();

			if (continuar.equals(true))
				super.siguienteActionPerformed(anterior, siguiente, finalizar);
		}
	}

	/**
	 * Cofirma un fichero dado
	 * @return	true o false indicando si se ha cofirmado correctamente
	 */
	public Boolean cofirmaFichero() {
		//comprobaci�n de la ruta de fichero de entrada.
		String ficheroDatos = campoDatos.getText();
		String ficheroFirma = campoFirma.getText();

		if (ficheroDatos == null || ficheroDatos.equals("")){
			JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.datos.vacio"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (!new File(ficheroDatos).exists() && !new File(ficheroDatos).isFile()) {
			JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.datos"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (ficheroFirma == null || ficheroFirma.equals("")){
			JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.firma.vacio"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (!new File(ficheroFirma).exists() && !new File(ficheroFirma).isFile()){
			JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.firma"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			InputStream dataIs = null;
			InputStream signIs = null;
			try {
				String intText = ".cosign";
				byte[] coSignedData = null;
				byte[] data = null;
				byte[] sign = null;
				MultisignUtils msUtils = new MultisignUtils();
				AOKeyStoreManager keyStoreManager = msUtils.getAOKeyStoreManager(kssc,this);

				// Recuperamos la clave del certificado
				PrivateKeyEntry keyEntry = msUtils.getPrivateKeyEntry(kssc, keyStoreManager, this);

				dataIs = new FileInputStream(ficheroDatos);
				data = AOUtil.getDataFromInputStream(dataIs);
				
				signIs = new FileInputStream(ficheroFirma);
				sign = AOUtil.getDataFromInputStream(signIs);
				
				AOSigner signer = AOSignerFactory.getSigner(sign);
				if (signer == null) {
					JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.manejador"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (!signer.isValidDataFile(data)) {
					JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.fichero"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (!signer.isSign(sign)) {
					JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error.fichero.soportado"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
					return false;
				}
				try {
					coSignedData = cosignOperation(signer, data, sign, keyEntry, ficheroDatos);
				} catch (AOException e) {
					logger.severe(e.toString());
					JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.cofirma.error"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
					return false;
				}
				
				final File savedFile = SelectionDialog.saveDataToFile(coSignedData,
				        signer.getSignedName(ficheroDatos, intText), null, this);
				// Si el usuario cancela el guardado de los datos, no nos desplazamos a la ultima pantalla
				if (savedFile == null) {
					return false;
				}
				
			} catch (Exception e) {
				logger.severe(e.toString());
				JOptionPane.showMessageDialog(this, Messages.getString("Wizard.multifirma.simple.error"), Messages.getString("error"), JOptionPane.ERROR_MESSAGE);
				return false;
			} finally {
				if (dataIs != null) {
					try { dataIs.close(); } catch (Exception e) {}
				}
				if (signIs != null) {
					try { signIs.close(); } catch (Exception e) {}
				}
			}
		}

		return true;
	}

	/**
	 * Cofirma de un fichero de datos.
	 * @param dataFile Fichero de datos a firma.
	 * @param keyEntry Clave de firma.
	 * @param filepath Ruta del fichero firmado.
	 * @return Contenido de la firma.
	 * @throws FileNotFoundException No se encuentra el fichero de datos.
	 * @throws AOException Ocurrio un error durante el proceso de firma.
	 */
	private byte[] cosignOperation(AOSigner signer, byte[] data, byte[] sign, PrivateKeyEntry keyEntry, String filepath) throws FileNotFoundException, AOException {
		
		Properties prop = GeneralConfig.getSignConfig();
		prop.setProperty("uri", filepath);
		
		// Respetaremos si la firma original contenia o no los datos firmados
		prop.setProperty("mode", signer.getData(sign) == null ? AOSignConstants.SIGN_MODE_EXPLICIT : AOSignConstants.SIGN_MODE_IMPLICIT);
		
		// Realizamos la cofirma
		return signer.cosign(data, sign, GeneralConfig.getSignAlgorithm(),	keyEntry, prop);
	}
}
