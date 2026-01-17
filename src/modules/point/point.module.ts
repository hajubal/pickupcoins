import { Module } from '@nestjs/common';
import { PointService } from './point.service';
import { ExchangeService } from './exchange.service';

@Module({
  providers: [PointService, ExchangeService],
  exports: [PointService, ExchangeService],
})
export class PointModule {}
