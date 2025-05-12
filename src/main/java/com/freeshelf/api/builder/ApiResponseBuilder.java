package com.freeshelf.api.builder;


import com.freeshelf.api.dto.BaseApiResponse;
import com.freeshelf.api.dto.Metadata;
import com.freeshelf.api.dto.Status;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.utils.Constants;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApiResponseBuilder {

  private final Tracer tracer;
  private final UserMgmtMapper mapper;

  public BaseApiResponse buildSuccessApiResponse(String statusMessage) {
    return new BaseApiResponse()
        .metadata(new Metadata().timestamp(Instant.now())
            .traceId(null != tracer.currentSpan()
                ? Objects.requireNonNull(tracer.currentSpan()).context().traceId()
                : ""))
        .status(new Status().statusCode(HttpStatus.OK.value()).statusMessage(statusMessage)
            .statusMessageKey(Constants.RESPONSE_MESSAGE_KEY_SUCCESS));
  }

}
