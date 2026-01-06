package ec.edu.sistemalicencias.service;

import ec.edu.sistemalicencias.dao.ConductorDAO;
import ec.edu.sistemalicencias.dao.LicenciaDAO;
import ec.edu.sistemalicencias.dao.PruebaPsicometricaDAO;
import ec.edu.sistemalicencias.model.TipoLicenciaConstantes;
import ec.edu.sistemalicencias.model.entities.Conductor;
import ec.edu.sistemalicencias.model.entities.Licencia;
import ec.edu.sistemalicencias.model.entities.PruebaPsicometrica;
import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;
import ec.edu.sistemalicencias.model.exceptions.DocumentoInvalidoException;
import ec.edu.sistemalicencias.model.exceptions.LicenciaException;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio que encapsula la lógica de negocio para la gestión de licencias.
 * Implementa las reglas de negocio y validaciones complejas.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 */
public class LicenciaService {

    // DAOs necesarios (Inyección de dependencias manual)
    private final ConductorDAO conductorDAO;
    private final PruebaPsicometricaDAO pruebaPsicometricaDAO;
    private final LicenciaDAO licenciaDAO;

    /**
     * Constructor que inicializa los DAOs
     */
    public LicenciaService() {
        this.conductorDAO = new ConductorDAO();
        this.pruebaPsicometricaDAO = new PruebaPsicometricaDAO();
        this.licenciaDAO = new LicenciaDAO();
    }

    /**
     * Registra un nuevo conductor en el sistema
     * @param conductor Conductor a registrar
     * @return ID del conductor registrado
     * @throws LicenciaException Si hay errores de validación o persistencia
     */
    public Long registrarConductor(Conductor conductor) throws LicenciaException {
        try {
            // Validar datos del conductor
            conductor.validar();

            // Verificar que no exista otro conductor con la misma cédula
            Conductor conductorExistente = conductorDAO.buscarPorCedula(conductor.getCedula());
            if (conductorExistente != null) {
                throw new DocumentoInvalidoException(
                        "Ya existe un conductor registrado con la cédula: " + conductor.getCedula()
                );
            }

            // Guardar conductor
            Long id = conductorDAO.guardar(conductor);
            conductor.setId(id);

            return id;

        } catch (DocumentoInvalidoException e) {
            throw e; // Reenviar excepción de validación
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al registrar conductor", e);
        }
    }

    /**
     * Actualiza los datos de un conductor existente
     * @param conductor Conductor con datos actualizados
     * @throws LicenciaException Si hay errores
     */
    public void actualizarConductor(Conductor conductor) throws LicenciaException {
        try {
            // Validar datos
            conductor.validar();

            // Verificar que el conductor existe
            if (conductor.getId() == null || conductorDAO.buscarPorId(conductor.getId()) == null) {
                throw new DocumentoInvalidoException("El conductor no existe en el sistema");
            }

            // Actualizar
            conductorDAO.guardar(conductor);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al actualizar conductor", e);
        }
    }

    /**
     * Valida los documentos de un conductor
     * @param conductorId ID del conductor
     * @param documentosValidos Resultado de la validación
     * @param observaciones Observaciones de la validación
     * @throws LicenciaException Si hay errores
     */
    public void validarDocumentos(Long conductorId, boolean documentosValidos, String observaciones)
            throws LicenciaException {
        try {
            Conductor conductor = conductorDAO.buscarPorId(conductorId);
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }

            conductor.setDocumentosValidados(documentosValidos);
            conductor.setObservaciones(observaciones);

            conductorDAO.guardar(conductor);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al validar documentos", e);
        }
    }

    /**
     * Registra una prueba psicométrica para un conductor
     * @param prueba Prueba psicométrica a registrar
     * @return ID de la prueba registrada
     * @throws LicenciaException Si hay errores
     */
    public Long registrarPruebaPsicometrica(PruebaPsicometrica prueba) throws LicenciaException {
        try {
            // Verificar que el conductor existe
            Conductor conductor = conductorDAO.buscarPorId(prueba.getConductorId());
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }

            // Guardar prueba
            return pruebaPsicometricaDAO.guardar(prueba);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al registrar prueba psicométrica", e);
        }
    }

    /**
     * Emite una nueva licencia de conducir
     * @param conductorId ID del conductor
     * @param tipoLicencia Tipo de licencia a emitir (usar constantes de TipoLicenciaConstantes)
     * @param pruebaPsicometricaId ID de la prueba psicométrica (opcional)
     * @return Licencia emitida
     * @throws LicenciaException Si no se cumplen los requisitos
     */
    public Licencia emitirLicencia(Long conductorId, String tipoLicencia, Long pruebaPsicometricaId)
            throws LicenciaException {
        try {
            // 1. Verificar que el conductor existe y está validado
            Conductor conductor = conductorDAO.buscarPorId(conductorId);
            if (conductor == null) {
                throw new DocumentoInvalidoException("Conductor no encontrado");
            }

            if (!conductor.isDocumentosValidados()) {
                throw new DocumentoInvalidoException(
                        "No se puede emitir licencia: documentos del conductor no validados"
                );
            }

            // 2. Verificar edad mínima
            if (conductor.calcularEdad() < 18) {
                throw new DocumentoInvalidoException(
                        "No se puede emitir licencia: el conductor debe ser mayor de 18 años"
                );
            }

            // 3. Verificar prueba psicométrica si se proporcionó
            if (pruebaPsicometricaId != null) {
                PruebaPsicometrica prueba = pruebaPsicometricaDAO.buscarPorId(pruebaPsicometricaId);
                if (prueba == null) {
                    throw new DocumentoInvalidoException("Prueba psicométrica no encontrada");
                }

                if (!prueba.estaAprobado()) {
                    throw new DocumentoInvalidoException(
                            "No se puede emitir licencia: prueba psicométrica no aprobada (Promedio: " +
                                    String.format("%.2f", prueba.calcularPromedio()) + ")"
                    );
                }
            }

            // 4. Verificar que no tenga licencias vigentes del mismo tipo
            List<Licencia> licenciasExistentes = licenciaDAO.buscarPorConductor(conductorId);
            for (Licencia lic : licenciasExistentes) {
                if (tipoLicencia.equals(lic.getTipoLicencia()) && lic.estaVigente()) {
                    throw new DocumentoInvalidoException(
                            "El conductor ya tiene una licencia vigente de tipo " + TipoLicenciaConstantes.obtenerNombre(tipoLicencia)
                    );
                }
            }

            // 5. Crear y guardar la licencia
            Licencia nuevaLicencia = new Licencia(conductorId, tipoLicencia);
            nuevaLicencia.setPruebaPsicometricaId(pruebaPsicometricaId);
            nuevaLicencia.generarNumeroLicencia(conductor.getCedula());

            // Validar licencia
            nuevaLicencia.validar();

            // Guardar en base de datos
            Long licenciaId = licenciaDAO.guardar(nuevaLicencia);
            nuevaLicencia.setId(licenciaId);

            return nuevaLicencia;

        } catch (DocumentoInvalidoException e) {
            throw e;
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al emitir licencia", e);
        }
    }

    /**
     * Busca un conductor por cédula
     * @param cedula Número de cédula
     * @return Conductor encontrado o null
     * @throws LicenciaException Si hay errores
     */
    public Conductor buscarConductorPorCedula(String cedula) throws LicenciaException {
        try {
            return conductorDAO.buscarPorCedula(cedula);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductor", e);
        }
    }

    /**
     * Busca un conductor por ID
     * @param id ID del conductor
     * @return Conductor encontrado o null
     * @throws LicenciaException Si hay errores
     */
    public Conductor buscarConductorPorId(Long id) throws LicenciaException {
        try {
            return conductorDAO.buscarPorId(id);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductor", e);
        }
    }

    /**
     * Obtiene todos los conductores registrados
     * @return Lista de conductores
     * @throws LicenciaException Si hay errores
     */
    public List<Conductor> obtenerTodosConductores() throws LicenciaException {
        try {
            return conductorDAO.obtenerTodos();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener conductores", e);
        }
    }

    /**
     * Busca conductores por nombre
     * @param nombre Nombre a buscar
     * @return Lista de conductores
     * @throws LicenciaException Si hay errores
     */
    public List<Conductor> buscarConductoresPorNombre(String nombre) throws LicenciaException {
        try {
            return conductorDAO.buscarPorNombre(nombre);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar conductores", e);
        }
    }

    /**
     * Obtiene las pruebas psicométricas de un conductor
     * @param conductorId ID del conductor
     * @return Lista de pruebas
     * @throws LicenciaException Si hay errores
     */
    public List<PruebaPsicometrica> obtenerPruebasConductor(Long conductorId) throws LicenciaException {
        try {
            return pruebaPsicometricaDAO.buscarPorConductor(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener pruebas", e);
        }
    }

    /**
     * Obtiene la última prueba aprobada de un conductor
     * @param conductorId ID del conductor
     * @return Última prueba aprobada o null
     * @throws LicenciaException Si hay errores
     */
    public PruebaPsicometrica obtenerUltimaPruebaAprobada(Long conductorId) throws LicenciaException {
        try {
            return pruebaPsicometricaDAO.obtenerUltimaPruebaAprobada(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener prueba", e);
        }
    }

    /**
     * Busca una licencia por número
     * @param numeroLicencia Número de licencia
     * @return Licencia encontrada o null
     * @throws LicenciaException Si hay errores
     */
    public Licencia buscarLicenciaPorNumero(String numeroLicencia) throws LicenciaException {
        try {
            return licenciaDAO.buscarPorNumero(numeroLicencia);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al buscar licencia", e);
        }
    }

    /**
     * Obtiene las licencias de un conductor
     * @param conductorId ID del conductor
     * @return Lista de licencias
     * @throws LicenciaException Si hay errores
     */
    public List<Licencia> obtenerLicenciasConductor(Long conductorId) throws LicenciaException {
        try {
            return licenciaDAO.buscarPorConductor(conductorId);
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias", e);
        }
    }

    /**
     * Obtiene todas las licencias del sistema
     * @return Lista de licencias
     * @throws LicenciaException Si hay errores
     */
    public List<Licencia> obtenerTodasLicencias() throws LicenciaException {
        try {
            return licenciaDAO.obtenerTodas();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias", e);
        }
    }

    /**
     * Obtiene las licencias vigentes
     * @return Lista de licencias vigentes
     * @throws LicenciaException Si hay errores
     */
    public List<Licencia> obtenerLicenciasVigentes() throws LicenciaException {
        try {
            return licenciaDAO.obtenerLicenciasVigentes();
        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al obtener licencias vigentes", e);
        }
    }

    /**
     * Desactiva una licencia
     * @param licenciaId ID de la licencia
     * @param motivo Motivo de desactivación
     * @throws LicenciaException Si hay errores
     */
    public void desactivarLicencia(Long licenciaId, String motivo) throws LicenciaException {
        try {
            Licencia licencia = licenciaDAO.buscarPorId(licenciaId);
            if (licencia == null) {
                throw new DocumentoInvalidoException("Licencia no encontrada");
            }

            licencia.setActiva(false);
            licencia.setObservaciones(motivo);

            licenciaDAO.guardar(licencia);

        } catch (BaseDatosException e) {
            throw new LicenciaException("Error al desactivar licencia", e);
        }
    }
}
