package hello;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Harmen Wiersma on 08/02/2016.
 */

public interface CustomerRepository extends JpaRepository<Customer, Long>
{
	List<Customer> findByLastNameStartsWithIgnoreCase(String lastName);
}