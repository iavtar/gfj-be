package com.iavtar.gfj_be.repository;

import com.iavtar.gfj_be.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    boolean existsByClientName(String clientName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByClientCode(String clientCode);

    List<Client> findByAgent_Id(Long agentId);

    List<Client> findByAgent_Username(String agentUsername);

    long countByAgent_Id(Long agentId);

    Optional<Client> findByClientName(String clientName);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByPhoneNumber(String phoneNumber);

    @Query("SELECT c FROM Client c JOIN FETCH c.agent WHERE c.id = :clientId")
    Optional<Client> findByIdWithAgent(@Param("clientId") Long clientId);

    @Query("SELECT c FROM Client c JOIN FETCH c.agent")
    List<Client> findAllWithAgents();


    @Query("SELECT c FROM Client c JOIN FETCH c.agent WHERE c.agent.id = :agentId")
    List<Client> findByAgentIdWithAgent(@Param("agentId") Long agentId);

    List<Client> findByBusinessType(String businessType);

    List<Client> findByCity(String city);

    List<Client> findByState(String state);

    List<Client> findByCountry(String country);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clientName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Client> searchByClientNameContaining(@Param("keyword") String keyword);

    /**
     * Search clients by multiple fields
     */
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.clientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.businessType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.city) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Client> searchClients(@Param("keyword") String keyword);

    /**
     * Search clients for a specific agent
     */
    @Query("SELECT c FROM Client c WHERE c.agent.id = :agentId AND " +
            "(LOWER(c.clientName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.businessType) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Client> searchClientsByAgent(@Param("agentId") Long agentId, @Param("keyword") String keyword);

    /**
     * Check if client belongs to specific agent
     */
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE c.id = :clientId AND c.agent.id = :agentId")
    boolean existsByIdAndAgentId(@Param("clientId") Long clientId, @Param("agentId") Long agentId);

    /**
     * Find clients with specific agent role validation
     */
    @Query("SELECT c FROM Client c JOIN c.agent a JOIN a.roles r WHERE r.name = 'AGENT'")
    List<Client> findAllClientsWithValidAgents();

}
