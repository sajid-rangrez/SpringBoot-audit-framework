//package com.audit.audit_poc.abs;
//
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.transaction.annotation.Transactional;
//
//// Generic implementation
//public abstract class AbsBusiness<T extends AbsEntity> implements AbsService<T> {
//
//    // Child classes must provide the specific DAO
//    protected abstract AbsDao<T> getDao();
//
//    @Override
//    @Transactional
//    public T save(T entity) {
//        return getDao().save(entity);
//    }
//
//    @Override
//    public T findById(UUID id) {
//        return getDao().findById(id)
//                .orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));
//    }
//
//    @Override
//    public List<T> findAll() {
//        return getDao().findAll();
//    }
//
//    @Override
//    @Transactional
//    public void deleteById(UUID id) {
//        getDao().deleteById(id);
//    }
//}