package com.experiments.calvin

import java.util.UUID

import cats.Id
import cats.syntax.all._
import com.experiments.calvin.repositories.algebras.OrderRepositoryATM
import com.experiments.calvin.repositories.interpreters.inmemory.InMemoryOrderRepositoryATM
import com.experiments.calvin.repositories.{Order, _}
import com.experiments.calvin.services.{OrderError, OrderServiceATM}

import scala.util.Random

object InMemoryAppATM extends App {
  // commit to the in-memory implementation at the end of the world
  val idOrderRepository  =  InMemoryOrderRepositoryATM.asInstanceOf[OrderRepositoryATM.Aux[Id]]

  val orderService = OrderServiceATM[Id](idOrderRepository)

  // Usage
  val orderId = UUID.randomUUID()
  val result: Either[OrderError, Id[Option[Order]]] = for {
    _             <- orderService.create(Order(orderId, Placed, s"random-order-${Random.nextInt(1000)}"))
    _             <- orderService.updateStatus(orderId, Shipped)
    updatedOrder  <- orderService.get(orderId).asRight
  } yield updatedOrder

  println(result)
}
