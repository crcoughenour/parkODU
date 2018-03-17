package edu.odu.cs.gold.service;

import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import edu.odu.cs.gold.model.User;
import edu.odu.cs.gold.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean confirmationTokenExits(String confirmationToken) {
        //Predicate predicate = Predicates.equal("confirmationToken", confirmationToken);
        //User user = userRepository.countByPredicate(confirmationToken);
        //int numTokens = userRepository.countByPredicate(predicate);
        //boolean isEnabled = userRepository.findByConfirmationToken(confirmationToken).getEnabled();
        //if (numTokens != 0 &&  isEnabled == false) {
        //    return true;
        //}
        //else {
            return false;
        //}
    }

    public boolean userExists(String email) {
        Predicate predicate = Predicates.equal("email", email);
        int numUsers = userRepository.countByPredicate(predicate);
        if (numUsers > 0) {
            return true;
        }
        return false;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(int id) {
        Predicate predicate = Predicates.equal("id", id);
        userRepository.deleteByPredicate(predicate);
    }

    public void refresh(String userId) {

    }
}