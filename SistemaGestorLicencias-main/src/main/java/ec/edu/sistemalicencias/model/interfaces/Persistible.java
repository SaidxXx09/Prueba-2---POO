package ec.edu.sistemalicencias.model.interfaces;

import ec.edu.sistemalicencias.model.exceptions.BaseDatosException;

/**
 * Interface que define el contrato para entidades persistibles en base de datos.
 * Abstrae las operaciones CRUD básicas.
 *
 * @author Sistema Licencias Ecuador
 * @version 1.0
 * @param <T> Tipo de entidad que se persistirá
 */
public interface Persistible<T> {

    /**
     * Guarda o actualiza la entidad en la base de datos.
     * @param entidad Objeto a persistir
     * @return ID generado o actualizado
     * @throws BaseDatosException Si ocurre un error en la operación de base de datos
     */
    Long guardar(T entidad) throws BaseDatosException;

    /**
     * Busca una entidad por su ID.
     * @param id Identificador único de la entidad
     * @return Entidad encontrada o null si no existe
     * @throws BaseDatosException Si ocurre un error en la consulta
     */
    T buscarPorId(Long id) throws BaseDatosException;

    /**
     * Elimina una entidad de la base de datos.
     * @param id Identificador de la entidad a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws BaseDatosException Si ocurre un error en la operación
     */
    boolean eliminar(Long id) throws BaseDatosException;
}
