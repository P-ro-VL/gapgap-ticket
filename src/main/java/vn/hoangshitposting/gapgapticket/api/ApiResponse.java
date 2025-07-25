package vn.hoangshitposting.gapgapticket.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
  ApiMeta meta;
  T data;

  public ResponseEntity<ApiResponse<T>> toResponseEntity(HttpStatus status) {
      return new ResponseEntity<>(this, status);
  }
}