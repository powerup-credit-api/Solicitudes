package co.crediyacorp.model.transactions;

import reactor.core.publisher.Mono;

public interface TransactionalWrapper {

    <T> Mono<T> executeInTransaction(Mono<T> action);
}
