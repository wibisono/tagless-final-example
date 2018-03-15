package com.experiments.calvin.services

import java.util.UUID

import cats.Monad
import cats.syntax.all._
import com.experiments.calvin.repositories.algebras.OrderRepositoryATM
import com.experiments.calvin.repositories.{Order, OrderStatus}

import scala.language.higherKinds

class OrderServiceATM[F[_]: Monad](orderRepository: OrderRepositoryATM.Aux[F]) {

  def get(orderId: UUID): F[Option[Order]] = orderRepository.get(orderId)

  def remove(orderId: UUID): F[Option[Order]] = orderRepository.remove(orderId)

  def create(order: Order): F[Either[OrderError, Order]] =
    get(order.id).flatMap {
      case None =>
        orderRepository.put(order).map(_.asRight)

      case Some(existingOrder) =>
        Monad[F].pure(OrderAlreadyExists(existingOrder.id).asLeft)
    }

  def updateStatus(orderId: UUID, status: OrderStatus): F[Either[OrderError, Order]] =
    get(orderId).flatMap {
      case None =>
        Monad[F].pure(OrderDoesNotExist(orderId).asLeft)

      case Some(order) =>
        val updatedOrder = order.copy(status = status)
        orderRepository
          .put(updatedOrder)
          .map(_.asRight)
    }
}

object OrderServiceATM {
  def apply[F[_]: Monad](orderRepository: OrderRepositoryATM.Aux[F]) =
    new OrderServiceATM[F](orderRepository)
}
