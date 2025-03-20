<template>
      <div class="container mx-auto">
            <div class="row g-4 justify-content-center">
                  <div class="col-md-8">
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Danh Sách Hóa Đơn</h3>
                              <table class="w-full border-collapse">
                                    <thead class="bg-gray-300">
                                          <tr>
                                                <th class="border px-4 py-2">Mã Hóa Đơn</th>
                                                <th class="border px-4 py-2">Tên Nhân Viên</th>
                                                <th class="border px-4 py-2">Tên Khách Hàng</th>
                                                <th class="border px-4 py-2">Trạng Thái</th>
                                                <th class="border px-4 py-2">Tổng Tiền</th>
                                          </tr>
                                    </thead>
                                    <tbody>
                                          <tr v-for="(invoice, index) in paginatedInvoices" :key="invoice.maDonHang"
                                                :class="['cursor-pointer hover:bg-gray-200', { 'bg-gray-400': selectedInvoice.maDonHang === invoice.maDonHang }]"
                                                @click="selectInvoice(invoice)">
                                                <td class="border px-4 py-2">{{ invoice.maDonHang }}</td>
                                                <td class="border px-4 py-2">{{ invoice.nhanVien ?
                                                      invoice.nhanVien.tenDangNhap : 'Không có' }}</td>
                                                <td class="border px-4 py-2">{{ invoice.khachHang ?
                                                      invoice.khachHang.hoTen : 'Khách vãng lai' }}</td>
                                                <td class="border px-4 py-2">{{ invoice.trangThaiDonHang }}</td>
                                                <td class="border px-4 py-2">{{ invoice.tongTien ?
                                                      invoice.tongTien.toLocaleString() : '0' }} đ</td>
                                          </tr>
                                    </tbody>
                              </table>

                              <div class="pagination-container" v-if="invoices.length > 0">
                                    <button @click="prevPageInvoice" :disabled="currentPageInvoice === 1"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-left"></i>
                                    </button>

                                    <div class="pagination-pages">
                                          <button v-for="page in totalPagesInvoice" :key="page"
                                                @click="changePageInvoice(page)"
                                                :class="['pagination-page', { 'active': currentPageInvoice === page }]">
                                                {{ page }}
                                          </button>
                                    </div>

                                    <button @click="nextPageInvoice"
                                          :disabled="currentPageInvoice === totalPagesInvoice" class="pagination-btn">
                                          <i class="bi bi-chevron-right"></i>
                                    </button>

                                    <select v-model="pageSizeInvoice" class="pagination-select"
                                          @change="changePageInvoice(1)">
                                          <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                                                {{ option }} / trang
                                          </option>
                                    </select>
                              </div>
                        </div>



                        <!-- Chi Tiết Đơn Hàng -->
                        <!-- Chi Tiết Đơn Hàng -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Chi Tiết Đơn Hàng</h3>
                              <div v-if="errorMessage" class="alert alert-danger">
                                    {{ errorMessage }}
                              </div>

                              <!-- Hiển thị chi tiết sản phẩm trong đơn hàng -->
                              <!-- Hiển thị chi tiết sản phẩm trong đơn hàng -->
                              <div v-if="orderDetails.length > 0">
                                    <table class="w-full border-collapse">
                                          <thead class="bg-gray-300">
                                                <tr>
                                                      <th class="border px-4 py-2">Mã Sản Phẩm</th>
                                                      <th class="border px-4 py-2">Tên Sản Phẩm</th>
                                                      <th class="border px-4 py-2">Số Lượng</th>
                                                      <th class="border px-4 py-2">Thành Tiền</th>
                                                      <th class="border px-4 py-2">Hành Động</th>
                                                </tr>
                                          </thead>
                                          <tbody>
                                                <tr v-for="(spct, index) in orderDetails" :key="spct.id">
                                                      <!-- Mã sản phẩm -->
                                                      <td class="border px-4 py-2">
                                                            {{ spct?.maSPCT || 'N/A' }}
                                                      </td>

                                                      <!-- Tên sản phẩm -->
                                                      <td class="border px-4 py-2">
                                                            {{ spct?.tenSanPham || 'N/A' }}
                                                      </td>

                                                      <!-- Số lượng -->
                                                      <td class="border px-4 py-2">
                                                            <input v-model="spct.soLuong" type="number" min="1"
                                                                  @change="updateQuantity(spct)" class="form-control" />
                                                      </td>

                                                      <!-- Thành tiền -->
                                                      <td class="border px-4 py-2">
                                                            {{ (spct.soLuong * (spct.giaBan || 0)).toLocaleString() }} đ
                                                      </td>

                                                      <!-- Nút xóa -->
                                                      <td class="border px-4 py-2">
                                                            <button @click="removeFromOrder(spct.id)"
                                                                  class="btn btn-danger">Xóa</button>
                                                      </td>
                                                </tr>
                                          </tbody>

                                    </table>
                              </div>


                              <div v-if="orderDetails.length === 0">
                                    <p class="text-center">Không có chi tiết đơn hàng để hiển thị.</p>
                              </div>

                              <div v-if="loading" class="text-center">Đang tải chi tiết đơn hàng...</div>

                              <div v-if="!loading" class="text-right font-bold mt-4">
                                    Tổng Tiền: {{ totalAmount.toLocaleString() }} đ
                              </div>
                        </div>




                        <!-- Tìm Kiếm Sản Phẩm -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Tìm kiếm Sản Phẩm</h3>
                              <input type="text" v-model="searchQuerySPCT" @input="searchProducts"
                                    class="form-control mb-4 border rounded-lg p-2"
                                    placeholder="Tìm kiếm sản phẩm..." />
                              <table v-if="!loading && paginatedSanPhamCTList.length > 0"
                                    class="table table-striped table-hover">
                                    <thead class="table-dark">
                                          <tr class="text-center">
                                                <th>STT</th>
                                                <th>Tên sản phẩm</th>
                                                <th>Màu sắc</th>

                                                <th>Size</th>
                                                <th>Chất liệu</th>
                                                <th>Danh Mụcy</th>
                                                <th>Thương hiệu</th>
                                                <th>Đế giày</th>
                                                <th>Số lượng</th>
                                                <th>Giá bán</th>
                                          </tr>
                                    </thead>
                                    <tbody>
                                          <tr v-for="(spct, index) in paginatedSanPhamCTList" :key="spct.id"
                                                class="align-middle" @click="addToCart(spct)">
                                                <td class="text-center">{{ (currentPageSPCT - 1) * pageSizeSPCT + index
                                                      + 1 }}</td>
                                                <td>{{ spct.tenSanPham }}</td>

                                                <td>{{ spct.tenMau || "Không có" }}</td>

                                                <td class="text-center">{{ spct.tenSize }}</td>
                                                <td class="text-center">{{ spct.tenDeGiay ? spct.tenDeGiay : "Không có"
                                                      }}</td>
                                                <td class="text-center">{{ spct.tenThuongHieu ? spct.tenThuongHieu :
                                                      "Không có" }}</td>
                                                <td class="text-center">{{ spct.tenDanhMuc ? spct.tenDanhMuc : "Không có" }}</td>
                                                <td class="text-center">{{ spct.tenChatLieu ? spct.tenChatLieu : "Không có" }}</td>

                                                <td class="text-center">{{ spct.soLuong }}</td>
                                                <td class="text-center">{{ spct.giaBan.toLocaleString() }} đ</td>

                                          </tr>
                                    </tbody>
                              </table>
                              <div class="pagination-container" v-if="sanPhamCTList.length > 0">
                                    <button @click="prevPage" :disabled="currentPageSPCT === 1" class="pagination-btn">
                                          <i class="bi bi-chevron-left"></i>
                                    </button>
                                    <div class="pagination-pages">
                                          <button v-for="page in visiblePages" :key="page" @click="changePage(page)"
                                                :class="['pagination-page', { 'active': currentPageSPCT === page }]">
                                                {{ page }}
                                          </button>
                                    </div>
                                    <button @click="nextPage" :disabled="currentPageSPCT === totalPages"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-right"></i>
                                    </button>
                                    <select v-model="pageSizeSPCT" class="pagination-select"
                                          @change="currentPageSPCT = 1">
                                          <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                                                {{ option }} / trang
                                          </option>
                                    </select>
                              </div>
                        </div>
                  </div>

                  <div class="col-md-4">
                        <!-- Chi Tiết Hóa Đơn -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Chi Tiết Hóa Đơn</h3>
                              <div class="mb-4">
                                    <label class="block font-semibold">Nhân Viên</label>
                                    <input type="text" class="form-control"
                                          :value="selectedInvoice?.nhanVien?.tenDangNhap || 'Không có'" />
                              </div>
                              <div class="mb-4">
                                    <label class="block font-semibold">Khách Hàng</label>
                                    <div class="flex items-center">
                                          <input type="text" class="form-control"
                                                :value="selectedInvoice?.khachHang?.hoTen" readonly />
                                          <button class="btn btn-success ml-2" @click="handleAddKhachHang">+</button>
                                    </div>
                              </div>
                              <div class="mb-4">
                                    <label class="block font-semibold">Mã Hóa Đơn</label>
                                    <p class="text-danger">{{ selectedInvoice?.maDonHang || "Chưa chọn" }}</p>
                              </div>
                              <div class="mb-4">
                                    <label class="block font-semibold">Tổng Tiền</label>
                                    <p class="text-danger">{{ totalAmount.toLocaleString() + ' đ' }}</p>
                              </div>
                              <div class="mb-4">
                                    <label class="block font-semibold">Phương Thức Thanh Toán</label>
                                    <div class="flex items-center">
                                          <input type="radio" name="payment" class="mr-2" v-model="paymentMethod"
                                                value="cash" /> Tiền Mặt
                                          <input type="radio" name="payment" class="ml-4 mr-2" v-model="paymentMethod"
                                                value="bank-transfer" /> Chuyển Khoản
                                    </div>
                              </div>

                              <div class="mb-4">
                                    <label class="block font-semibold">Mã Giảm Giá (Nếu Có)</label>
                                    <div class="flex items-center space-x-2">
                                          <input type="text" class="form-control" v-model="discountCode" />
                                          <button class="btn btn-warning" @click="applyDiscount">Áp Dụng</button>
                                    </div>
                              </div>

                              <div class="d-flex justify-content-between">
                                    <button class="btn btn-secondary" @click="cancelInvoice">🗑️ Hủy Hóa Đơn</button>
                                    <button class="btn btn-primary " @click="createInvoice">🆕 Tạo Hóa Đơn</button>
                              </div>
                              <button class="btn btn-success w-100 mt-4" @click="payInvoice">Thanh Toán</button>
                        </div>

                        <!-- Danh sách khách hàng -->
                        <div class="bg-white p-6 rounded-lg shadow-lg mb-8 border border-gray-300">
                              <h3 class="font-semibold text-2xl mb-4">Danh sách khách hàng</h3>
                              <div class="d-flex justify-content-between align-items-center mb-4">
                                    <div class="d-flex w-50">
                                          <input v-model="searchQueryKH" class="form-control me-2" type="text"
                                                placeholder="Tìm kiếm khách hàng..." />
                                    </div>
                              </div>

                              <div v-if="loading" class="alert alert-info text-center">Đang tải dữ liệu...</div>

                              <!-- Bảng danh sách khách hàng -->
                              <table v-if="!loading && khachHangList.length > 0"
                                    class="table table-striped table-hover">
                                    <thead class="table-dark">
                                          <tr class="text-center">
                                                <th>STT</th>
                                                <th>Tên Khách Hàng</th>
                                                <th>Số Điện Thoại</th>
                                          </tr>
                                    </thead>
                                    <tbody>
                                          <tr v-for="(khachHang, index) in paginatedKhachHangList" :key="khachHang.id"
                                                class="align-middle">
                                                <td class="text-center">{{ (currentPageKH - 1) * pageSizeKH + index + 1
                                                      }}</td>
                                                <td>{{ khachHang.hoTen }}</td>
                                                <td>{{ khachHang.soDienThoai || 'Không có' }}</td>
                                          </tr>
                                    </tbody>
                              </table>

                              <!-- Pagination for Customers -->
                              <div class="pagination-container" v-if="khachHangList.length > 0">
                                    <button @click="prevPageCustomer" :disabled="currentPageKH === 1"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-left"></i>
                                    </button>
                                    <div class="pagination-pages">
                                          <button v-for="page in visiblePages" :key="page"
                                                @click="changePageCustomer(page)"
                                                :class="['pagination-page', { 'active': currentPageKH === page }]">
                                                {{ page }}
                                          </button>
                                    </div>
                                    <button @click="nextPageCustomer" :disabled="currentPageKH === totalPages"
                                          class="pagination-btn">
                                          <i class="bi bi-chevron-right"></i>
                                    </button>
                                    <select v-model="pageSizeKH" class="pagination-select" @change="currentPageKH = 1">
                                          <option v-for="option in [5, 10, 15, 20]" :key="option" :value="option">
                                                {{ option }} / trang
                                          </option>
                                    </select>
                              </div>
                        </div>
                  </div>
            </div>
      </div>
</template>

<script>
import { useRouter } from 'vue-router';
import axios from "axios";
import { ref, onMounted, computed, watch } from "vue";

export default {
      setup() {
            const router = useRouter();
            const invoices = ref([]);
            const sanPhamCTList = ref([]);
            const selectedDanhMuc = ref("");
            const selectedThuongHieu = ref("");
            const selectedChatLieu = ref("");
            const selectedDeGiay = ref("");
            const danhMucList = ref([]);
            const thuongHieuList = ref([]);
            const chatLieuList = ref([]);
            const deGiayList = ref([]);
            const selectedKhachHang = ref(null); // Chọn khách hàng khi tạo hóa đơn
            const khachHangList = ref([]);
            const loading = ref(false);
            const errorMessage = ref("");
            const searchQuery = ref("");
            const searchQueryKH = ref("");
            const searchQuerySPCT = ref("");
            const orderDetails = ref([]); // Lưu trữ các sản phẩm trong chi tiết đơn hàng
            const paymentMethod = ref("");  // Khai báo paymentMethod dưới dạng ref
            const discountCode = ref("");
            const pageSize = ref(5);
            const currentPage = ref(1);
            const pageSizeKH = ref(5);
            const currentPageKH = ref(1);
            const pageSizeSPCT = ref(5);
            const currentPageSPCT = ref(1);
            const cartItems = ref([]); // Giỏ hàng
            const khachHangId = ref(2); // ID khách hàng (có thể lấy từ session hoặc từ router)
            const selectedInvoice = ref({});
            const selectedInvoiceCT = ref({});
            const selectedTrangThai = ref(null);


            // const fetchOrderDetails = async (invoiceId) => {
            //   console.log("🟡 Đang lấy chi tiết đơn hàng cho ID:", invoiceId);

            //   if (!invoiceId) {
            //     console.error("🔴 Lỗi: ID hóa đơn không hợp lệ!");
            //     return;
            //   }

            //   try {
            //     const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/dh/${invoiceId}`);
            //     console.log("✅ Dữ liệu trả về từ API:", response.data);

            //     if (response.data && Array.isArray(response.data.sanPhamChiTiet)) {
            //       orderDetails.value = response.data.sanPhamChiTiet;
            //     } else {
            //       console.error("🔴 Lỗi: Dữ liệu `sanPhamChiTiet` không hợp lệ!");
            //       orderDetails.value = [];
            //     }
            //   } catch (error) {
            //     console.error("🔴 Lỗi khi lấy chi tiết đơn hàng:", error);
            //     errorMessage.value = "Không thể tải chi tiết đơn hàng.";
            //   } finally {
            //     loading.value = false;
            //   }
            // };

            const fetchOrderDetails = async (invoiceId) => {
                  console.log("🔄 Đang lấy chi tiết hóa đơn:", invoiceId);

                  if (!invoiceId) {
                        console.error("⚠ ID hóa đơn không hợp lệ!");
                        return;
                  }

                  try {
                        const response = await axios.get(`http://localhost:8080/don-hang-chi-tiet/${invoiceId}`);
                        console.log("✅ Dữ liệu chi tiết đơn hàng:", response.data);

                        // Kiểm tra dữ liệu và cập nhật danh sách sản phẩm
                        if (response.data && Array.isArray(response.data.sanPhamChiTiet)) {
                              orderDetails.value = response.data.sanPhamChiTiet;
                        } else {
                              console.error("⚠ Dữ liệu không hợp lệ!");
                              orderDetails.value = [];
                        }
                  } catch (error) {
                        console.error("❌ Lỗi khi lấy chi tiết đơn hàng:", error);
                        errorMessage.value = "Không thể tải chi tiết đơn hàng.";
                  }
            };


            // Thêm sản phẩm vào giỏ hàng
            const addProductToInvoice = async (productId, quantity) => {
                  try {
                        const response = await axios.post('http://localhost:8080/don-hang-chi-tiet', {
                              donHang: { id: selectedInvoice.value.id },
                              sanPhamChiTiet: { id: productId },
                              soLuong: quantity,
                        });
                        if (response.status === 200) {
                              fetchOrderDetails(selectedInvoice.value.id);
                              alert("Sản phẩm đã được thêm vào hóa đơn thành công.");
                        }
                  } catch (error) {
                        alert("Không thể thêm sản phẩm vào hóa đơn.");
                  }
            };




            const addToCart = async (spct) => {
                  if (!selectedInvoice.value.id) {
                        alert("⚠ Vui lòng chọn hóa đơn trước khi chọn sản phẩm!");
                        return;
                  }

                  // Nhập số lượng
                  const quantity = parseInt(prompt("Nhập số lượng sản phẩm:"), 10);
                  if (!quantity || isNaN(quantity) || quantity <= 0) {
                        alert("⚠ Vui lòng nhập số lượng hợp lệ!");
                        return;
                  }

                  // Gửi dữ liệu lên API để thêm sản phẩm vào HDCT
                  try {
                        const response = await axios.post("http://localhost:8080/don-hang-chi-tiet", {
                              donHang: { id: selectedInvoice.value.id },
                              sanPhamChiTiet: { id: spct.id },
                              soLuong: quantity
                        });

                        if (response.status === 200) {
                              alert("✅ Sản phẩm đã được thêm vào hóa đơn!");
                              fetchOrderDetails(selectedInvoice.value.id); // Cập nhật lại danh sách sản phẩm trong HDCT
                        }
                  } catch (error) {
                        alert("❌ Không thể thêm sản phẩm vào hóa đơn.");
                        console.error("Lỗi khi thêm vào HDCT:", error);
                  }
            };

            const fetchGioHang = async () => {
                  loading.value = true;
                  errorMessage.value = "";

                  try {
                        const response = await axios.get(`http://localhost:8080/gio-hang/${khachHangId.value}`);
                        if (response.data && Array.isArray(response.data)) {
                              cartItems.value = response.data; // Gán dữ liệu giỏ hàng
                        } else {
                              throw new Error("Dữ liệu không hợp lệ");
                        }
                  } catch (error) {
                        console.error("Lỗi khi tải dữ liệu giỏ hàng:", error);
                        errorMessage.value = "Lỗi khi tải dữ liệu giỏ hàng.";
                  } finally {
                        loading.value = false;
                  }
            };

            // Xóa sản phẩm khỏi giỏ hàng
            const removeFromCart = async (id) => {
                  try {
                        await axios.delete(`http://localhost:8080/gio-hang/remove/${id}`);
                        fetchGioHang(); // Sau khi xóa, tải lại giỏ hàng
                  } catch (error) {
                        console.error("Lỗi khi xóa sản phẩm:", error);
                        errorMessage.value = "Lỗi khi xóa sản phẩm khỏi giỏ hàng.";
                  }
            };

            const updateQuantity = async (donHangChiTiet) => {
                  // Kiểm tra xem donHangChiTiet có giá trị hay không
                  if (!donHangChiTiet || !donHangChiTiet.id) {
                        console.error("❌ Không tìm thấy chi tiết đơn hàng hoặc ID không hợp lệ!");
                        alert("❌ Không tìm thấy chi tiết đơn hàng hoặc ID không hợp lệ!");
                        return;
                  }

                  if (donHangChiTiet.soLuong <= 0 || isNaN(donHangChiTiet.soLuong)) {
                        alert("Số lượng phải là số dương hợp lệ.");
                        return;
                  }

                  // Tạo đối tượng dữ liệu cập nhật với thông tin của chi tiết đơn hàng
                  const updatedItem = {
                        donHang: { id: donHangChiTiet.donHang.id },  // ID của đơn hàng
                        sanPhamChiTiet: { id: donHangChiTiet.sanPhamChiTiet.id },  // ID của sản phẩm chi tiết
                        soLuong: donHangChiTiet.soLuong  // Cập nhật số lượng của chi tiết đơn hàng
                  };

                  try {
                        // Gửi PUT request để cập nhật số lượng chi tiết đơn hàng
                        const response = await axios.put(`http://localhost:8080/don-hang-chi-tiet/update/${donHangChiTiet.id}`, updatedItem);

                        if (response.status === 200) {
                              alert("Số lượng chi tiết đơn hàng đã được cập nhật thành công.");
                              fetchOrderDetails(selectedInvoice.value.id);  // Cập nhật lại chi tiết đơn hàng
                        } else {
                              alert("❌ Cập nhật số lượng không thành công.");
                        }
                  } catch (error) {
                        console.error("❌ Lỗi khi cập nhật số lượng:", error);
                        alert("❌ Không thể cập nhật số lượng.");
                  }
            };

            const removeFromOrder = async (orderDetailId) => {
                  if (!orderDetailId) {
                        alert("❌ Vui lòng chọn chi tiết đơn hàng trước khi xóa.");
                        return;
                  }

                  try {
                        // Gửi yêu cầu xóa chi tiết đơn hàng (don_hang_chi_tiet) dựa trên orderDetailId
                        const responseDeleteDetails = await axios.delete(`http://localhost:8080/don-hang-chi-tiet/${orderDetailId}`);
                        console.log(`Response Status for deleting order details: ${responseDeleteDetails.status}`);

                        if (responseDeleteDetails.status === 200) {
                              alert("✅ Xóa chi tiết đơn hàng thành công.");
                        } else {
                              alert("❌ Xóa chi tiết đơn hàng không thành công.");
                              return;
                        }

                        // Nếu xóa thành công, tải lại chi tiết đơn hàng
                        fetchOrderDetails(selectedInvoiceCT.value.id);  // Cập nhật lại danh sách chi tiết đơn hàng
                  } catch (error) {
                        console.error("❌ Lỗi khi xóa chi tiết đơn hàng:", error);
                        alert("⚠ Không thể xóa chi tiết đơn hàng. Vui lòng thử lại.");
                  }
            };


            const selectOrderDetail = (orderDetail) => {
                  selectedInvoiceCT.value = orderDetail; // Lưu chi tiết đơn hàng vào selectedInvoiceCT
            };

            const updateQuantityInOrder = async (item) => {
                  try {
                        const updatedItem = {
                              sanPhamChiTiet: { id: item.sanPhamChiTiet.id },  // Send the product detail ID
                              soLuong: item.soLuong  // Send the updated quantity
                        };

                        const response = await axios.put(`http://localhost:8080/don-hang/${selectedInvoice.value.id}/update-quantity`, updatedItem);

                        if (response.status === 200) {
                              alert("Số lượng sản phẩm đã được cập nhật thành công.");
                              fetchOrderDetails(selectedInvoice.value.id);  // Refresh the order details
                        }
                  } catch (error) {
                        alert("Không thể cập nhật số lượng.");
                  }
            };


            // Tính tổng tiền giỏ hàng
            const totalAmount = computed(() => {
                  return orderDetails.value.reduce((total, item) => {
                        const giaBan = item?.giaBan || item?.sanPhamChiTiet?.giaBan || 0;
                        const soLuong = item?.soLuong || 0;
                        return total + (giaBan * soLuong);
                  }, 0);
            });





            // Fetch danh sách hóa đơn từ API
            const fetchInvoices = async () => {
                  try {
                        const response = await axios.get("http://localhost:8080/don-hang");
                        invoices.value = response.data;
                  } catch (error) {
                        console.error("Lỗi khi tải hóa đơn:", error);
                        errorMessage.value = "Không thể tải danh sách hóa đơn.";
                  }
            };
            const paginatedInvoices = computed(() => {
                  const startIndex = (currentPageInvoice.value - 1) * pageSizeInvoice.value;
                  return invoices.value.slice(startIndex, startIndex + pageSizeInvoice.value);
            });

            const selectInvoice = async (invoice) => {
                  console.log("Hóa đơn được chọn:", invoice); // Kiểm tra xem invoice có hợp lệ không

                  if (!invoice || !invoice.id) {
                        console.error("Invoice ID không hợp lệ");
                        alert("Hóa đơn không hợp lệ, không có ID.");
                        return;  // Nếu không có ID thì thoát
                  }

                  selectedInvoice.value = { ...invoice };  // Cập nhật hóa đơn được chọn
                  await fetchOrderDetails(invoice.id); // Lấy chi tiết đơn hàng từ API
            };



            watch(orderDetails, (newVal) => {
                  console.log("Cập nhật orderDetails: ", newVal);
            });

            const payInvoice = async () => {
                  if (!selectedInvoice.value.id) {
                        alert("Chưa chọn hóa đơn.");
                        return;
                  }
                  // Call API thanh toán
                  try {
                        await axios.post(`http://localhost:8080/thanh-toan/${selectedInvoice.value.id}`);
                        alert("Thanh toán thành công!");
                        fetchInvoices(); // Reload hóa đơn
                  } catch (error) {
                        console.error("Lỗi khi thanh toán:", error);
                        errorMessage.value = "Lỗi khi thanh toán.";
                  }
            };

            // const selectInvoice = async (invoice) => {
            //   console.log("Hóa đơn được chọn:", invoice);
            //   selectedInvoice.value = { ...invoice };

            //   // Gọi API để lấy các sản phẩm trong giỏ hàng của hóa đơn này
            //   try {
            //     const response = await axios.get(`http://localhost:8080/gio-hang/${invoice.id}`); // Giả sử `invoice.id` là khóa để lấy giỏ hàng
            //     cartItems.value = response.data || []; // Cập nhật danh sách sản phẩm trong giỏ hàng
            //   } catch (error) {
            //     console.error("Lỗi khi tải sản phẩm giỏ hàng:", error);
            //     errorMessage.value = "Không thể tải sản phẩm trong giỏ hàng cho hóa đơn này.";
            //   }
            // };


            const createInvoice = async () => {
                  try {
                        const invoiceData = {
                              maDonHang: `HD${Date.now()}`, // 🆕 Tạo mã đơn hàng tự động
                              khachHang: selectedKhachHang.value ? { id: selectedKhachHang.value.id } : null, // ✅ Không cần chọn khách hàng
                              nhanVien: { id: 1 }, // Giả định nhân viên có ID = 1
                              tongTien: 0, // Đảm bảo tổng tiền hợp lệ
                              chiPhiGiaoHang: 0, // 🛠 Fix lỗi bằng cách đặt giá trị mặc định
                              loaiDonHang: 0, // 🏠 Offline
                              trangThaiDonHang: "Chưa thanh toán",
                              ngayTao: new Date().toISOString(),
                              ngaySua: new Date().toISOString(),
                              phuongThucThanhToan: { id: 1 }, // Giả định có ID 1 cho phương thức thanh toán
                              voucher: null, // Có thể cập nhật sau nếu cần
                        };

                        console.log("🛠 Debug Invoice Data:", JSON.stringify(invoiceData, null, 2));

                        const response = await axios.post("http://localhost:8080/don-hang/create", invoiceData);
                        fetchInvoices(); // Load lại danh sách hóa đơn

                        alert("🎉 Hóa đơn mới đã được tạo thành công!");
                  } catch (error) {
                        console.error("❌ Lỗi khi tạo hóa đơn:", error);

                        if (error.response) {
                              console.log("💡 Response Data:", error.response.data);
                              alert(`🚨 Lỗi từ server: ${error.response.data.message || "Không xác định"}`);
                        }

                        errorMessage.value = "Không thể tạo hóa đơn. Vui lòng thử lại!";
                  }
            };

            const visiblePagesInvoice = computed(() => {
                  const maxPagesToShow = 5;
                  const total = Math.ceil(invoices.value.length / pageSizeInvoice.value);
                  const current = currentPageInvoice.value;

                  let start = Math.max(current - 2, 1);
                  let end = Math.min(current + 2, total);

                  if (start === 1) {
                        end = Math.min(start + maxPagesToShow - 1, total);
                  } else if (end === total) {
                        start = Math.max(total - maxPagesToShow + 1, 1);
                  }

                  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
            });
            const currentPageInvoice = ref(1);
            const pageSizeInvoice = ref(5);
            const totalPagesInvoice = computed(() => {
                  return Math.ceil(invoices.value.length / pageSizeInvoice.value) || 1;
            });


            const nextPageInvoice = () => {
                  if (currentPageInvoice.value < totalPagesInvoice.value) {
                        currentPageInvoice.value++;
                  }
            };

            const prevPageInvoice = () => {
                  if (currentPageInvoice.value > 1) {
                        currentPageInvoice.value--;
                  }
            };

            const changePageInvoice = (page) => {
                  if (page >= 1 && page <= totalPagesInvoice.value) {
                        currentPageInvoice.value = page;
                  }
            };

            //xoa hoa don
            const cancelInvoice = async () => {
                  if (!selectedInvoice.value.maDonHang) {
                        alert("❌ Vui lòng chọn hóa đơn trước khi hủy!");
                        return;
                  }

                  try {
                        const confirmDelete = confirm(`Bạn có chắc muốn hủy hóa đơn ${selectedInvoice.value.maDonHang}?`);
                        if (!confirmDelete) return;

                        await axios.delete(`http://localhost:8080/don-hang/${selectedInvoice.value.id}`);

                        alert("✅ Hóa đơn đã được hủy thành công!");
                        fetchInvoices(); // 🔄 Load lại danh sách hóa đơn
                        selectedInvoice.value = {}; // 🟢 Xóa dữ liệu hóa đơn được chọn
                  } catch (error) {
                        console.error("❌ Lỗi khi hủy hóa đơn:", error);
                        alert("⚠ Không thể hủy hóa đơn. Vui lòng thử lại!");
                  }
            };

            // Fetch product details (sanPhamCT)
            const fetchFilterData = async () => {
                  try {
                        const [danhMucRes, thuongHieuRes, chatLieuRes, deGiayRes] = await Promise.all([
                              axios.get("http://localhost:8080/danh-muc"),
                              axios.get("http://localhost:8080/thuong-hieu"),
                              axios.get("http://localhost:8080/chat-lieu"),
                              axios.get("http://localhost:8080/de-giay")
                        ]);

                        danhMucList.value = danhMucRes.data || [];
                        thuongHieuList.value = thuongHieuRes.data || [];
                        chatLieuList.value = chatLieuRes.data || [];
                        deGiayList.value = deGiayRes.data || [];
                  } catch (error) {
                        console.error("Lỗi khi tải dữ liệu bộ lọc: ", error);
                  }
            };

            const fetchSanPhamChiTiet = async () => {
                  loading.value = true;
                  try {
                        const response = await axios.get("http://localhost:8080/san-pham-chi-tiet", {
                              params: {
                                    keyword: searchQuerySPCT.value.trim(),
                                    tenDanhMuc: selectedDanhMuc.value,
                                    tenThuongHieu: selectedThuongHieu.value,
                                    tenChatLieu: selectedChatLieu.value,
                                    tenDeGiay: selectedDeGiay.value,
                              },
                        });
                        sanPhamCTList.value = response.data || [];
                  } catch (error) {
                        console.error("Lỗi khi tải sản phẩm: ", error);
                  } finally {
                        loading.value = false;
                  }
            };
            // 🔍 **Lọc danh sách sản phẩm theo từ khóa tìm kiếm**
            const filteredSanPhamCTList = computed(() => {
                  if (!searchQuerySPCT.value) return sanPhamCTList.value;
                  return sanPhamCTList.value.filter(spct =>
                        spct.tenSanPham.toLowerCase().includes(searchQuerySPCT.value.toLowerCase()) ||
                        (spct.tenThuongHieu && spct.tenThuongHieu.toLowerCase().includes(searchQuerySPCT.value.toLowerCase())) ||
                        (spct.tenDanhMuc && spct.tenDanhMuc.toLowerCase().includes(searchQuerySPCT.value.toLowerCase())) ||
                        (spct.tenChatLieu && spct.tenChatLieu.toLowerCase().includes(searchQuerySPCT.value.toLowerCase()))
                  );
            });




            // Fetch customers data
            const fetchKhachHang = async () => {
                  loading.value = true;
                  errorMessage.value = "";
                  try {
                        const response = await axios.get("http://localhost:8080/khach-hang");
                        if (response.data && Array.isArray(response.data)) {
                              khachHangList.value = response.data;
                        } else {
                              throw new Error("Dữ liệu không hợp lệ");
                        }
                  } catch (error) {
                        errorMessage.value = "Lỗi khi tải dữ liệu khách hàng. Vui lòng thử lại!";
                  } finally {
                        loading.value = false;
                  }
            };
            const filteredKhachHangList = computed(() => {
                  if (!searchQueryKH.value) return khachHangList.value; // Nếu không nhập từ khóa, trả về danh sách đầy đủ
                  return khachHangList.value.filter(khachHang =>
                        khachHang.hoTen.toLowerCase().includes(searchQueryKH.value.toLowerCase()) ||
                        khachHang.maKhachHang.toLowerCase().includes(searchQueryKH.value.toLowerCase()) ||
                        (khachHang.soDienThoai && khachHang.soDienThoai.includes(searchQueryKH.value))
                  );
            });
            const paginatedKhachHangList = computed(() => {
                  const startIndex = (currentPageKH.value - 1) * pageSizeKH.value;
                  return filteredKhachHangList.value.slice(startIndex, startIndex + pageSizeKH.value);
            });

            const totalPagesKH = computed(() => Math.ceil(filteredKhachHangList.value.length / pageSizeKH.value));

            const nextPageCustomer = () => {
                  if (currentPageKH.value < totalPagesKH.value) {
                        currentPageKH.value++;
                  }
            };

            const prevPageCustomer = () => {
                  if (currentPageKH.value > 1) {
                        currentPageKH.value--;
                  }
            };

            const changePageCustomer = (page) => {
                  currentPageKH.value = page;
            };

            const paginatedSanPhamCTList = computed(() => {
                  const startIndex = (currentPageSPCT.value - 1) * pageSizeSPCT.value;
                  return filteredSanPhamCTList.value.slice(startIndex, startIndex + pageSizeSPCT.value);
            });
            const totalPagesSPCT = computed(() => Math.ceil(filteredSanPhamCTList.value.length / pageSizeSPCT.value));

            // Total number of pages
            const totalPages = computed(() => {
                  return Math.ceil(sanPhamCTList.value.length / pageSizeSPCT.value);
            });

            const visiblePages = computed(() => {
                  const maxPagesToShow = 5;
                  const total = totalPages.value;
                  const current = currentPage.value;

                  let start = Math.max(current - 2, 1);
                  let end = Math.min(current + 2, total);

                  if (start === 1) {
                        end = Math.min(start + maxPagesToShow - 1, total);
                  } else if (end === total) {
                        start = Math.max(total - maxPagesToShow + 1, 1);
                  }

                  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
            });

            const handleAddKhachHang = () => {
                  router.push('/admin/customers/manage/add-khachhang');
            };
            // Pagination controls
            const nextPage = () => {
                  if (currentPageSPCT.value < totalPages.value) {
                        currentPageSPCT.value++;
                  }
            };

            const prevPage = () => {
                  if (currentPageSPCT.value > 1) {
                        currentPage.value--;
                  }
            };

            const changePage = (page) => {
                  currentPageSPCT.value = page;
            };

            // Handling product and customer view/edit
            const handleViewSanPhamChiTiet = (id) => {
                  router.push(`/admin/products/details/view-spct/${id}`);
            };

            const handleEditSanPhamChiTiet = (id) => {
                  router.push(`/admin/products/details/update-spct/${id}`);
            };
            watch(selectedInvoice, (newVal) => {
                  console.log("Hóa đơn đã chọn thay đổi:", newVal);
            });

            // Fetch data on mounted
            onMounted(() => {
                  fetchFilterData();
                  fetchSanPhamChiTiet();
                  fetchInvoices();
                  fetchKhachHang();
                  fetchGioHang();
                  fetchOrderDetails();

            });

            return {
                  router,
                  loading,
                  errorMessage,
                  searchQuery,
                  orderDetails,
                  //hoa don
                  selectOrderDetail,
                  removeFromOrder,
                  updateQuantityInOrder,
                  addProductToInvoice,
                  addToCart,
                  invoices,
                  fetchInvoices,
                  paginatedInvoices,
                  totalPagesInvoice,
                  prevPageInvoice,
                  nextPageInvoice,
                  changePageInvoice,
                  currentPageInvoice,
                  pageSizeInvoice,
                  visiblePagesInvoice,
                  createInvoice,
                  cancelInvoice,
                  selectedInvoice,
                  selectedInvoiceCT,

                  selectInvoice,

                  //san pham

                  paymentMethod,
                  discountCode,
                  fetchOrderDetails,
                  sanPhamCTList,
                  searchQuerySPCT,
                  khachHangList,
                  paginatedSanPhamCTList,
                  selectedDanhMuc,
                  selectedThuongHieu,
                  selectedChatLieu,
                  selectedDeGiay,
                  danhMucList,
                  thuongHieuList,
                  chatLieuList,
                  deGiayList,
                  totalPages,
                  visiblePages,
                  currentPageSPCT,
                  pageSizeSPCT,
                  handleEditSanPhamChiTiet,
                  handleViewSanPhamChiTiet,
                  fetchSanPhamChiTiet,
                  paginatedSanPhamCTList,
                  totalPagesSPCT,

                  //Khach hang
                  handleAddKhachHang,
                  searchQueryKH,
                  selectedKhachHang,
                  currentPageKH,
                  pageSizeKH,
                  currentPage,
                  pageSize,
                  nextPage,
                  prevPage,
                  changePage,
                  fetchKhachHang,
                  selectedInvoice,
                  selectedTrangThai,
                  cartItems,
                  removeFromCart,
                  updateQuantity,
                  totalAmount,
                  totalPagesKH,
                  paginatedKhachHangList,
                  nextPageCustomer,
                  prevPageCustomer,
                  changePageCustomer,
            };
      },
};
</script>

<style scoped>
/* Style for form inputs */
.form-control {
      width: 100%;
      padding: 0.75rem 1rem;
      margin-bottom: 12px;
}

/* Hover effect for table rows */
table tbody tr:hover {
      background-color: #f4f4f4;
}

/* Hover effect for buttons */
.btn:hover {
      transform: scale(1.05);
}

/* Margin between form groups */
.input-group {
      margin-bottom: 12px;
}

.bg-white {
      margin-bottom: 20px;
      padding: 20px;
}

.flex {
      display: flex;
      align-items: center;
}

.items-center {
      align-items: center;
}

.space-x-2 {
      margin-left: 8px;
}

.btn-success {
      margin-left: 8px;
}

/* Pagination styles */
.pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
}

.pagination-pages {
      display: flex;
      gap: 5px;
}

.pagination-btn {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 8px;
      background-color: #f0f0f0;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-btn:hover {
      background-color: #dcdcdc;
}

.pagination-btn:disabled {
      background-color: #e0e0e0;
      cursor: not-allowed;
}

.pagination-page {
      width: 40px;
      height: 40px;
      border: none;
      border-radius: 8px;
      background-color: #f8f9fa;
      color: #007bff;
      font-weight: bold;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

.pagination-page:hover {
      background-color: #e0e0e0;
}

.pagination-page.active {
      background-color: #007bff;
      color: white;
}

.pagination-select {
      width: 100px;
      height: 40px;
      border-radius: 8px;
      padding: 5px;
      border: 1px solid #ccc;
      cursor: pointer;
}




/* Giảm kích thước của các bảng */
table {
      font-size: 14px;
      /* Font chữ nhỏ hơn */
      width: 100%;
      /* Đảm bảo bảng chiếm hết chiều rộng của container */
      table-layout: fixed;
      /* Cải thiện hiển thị của bảng khi có nhiều dữ liệu */
}

/* Giảm padding của các ô trong bảng */
table th,
table td {
      padding: 8px 10px;
      /* Giảm khoảng cách trong các ô */
      text-align: center;
      /* Canh giữa các dữ liệu trong ô */
}

/* Giảm khoảng cách giữa các hàng */
table tbody tr {
      height: 35px;
      /* Đảm bảo hàng không chiếm quá nhiều chiều cao */
}

/* Cải thiện hiển thị các dòng */
table tbody tr:hover {
      background-color: #f4f4f4;
      /* Đảm bảo hàng được đánh dấu khi hover */
}

/* Thiết lập các bảng có thể cuộn ngang khi dữ liệu quá nhiều */
.table-wrapper {
      overflow-x: auto;
}

/* Điều chỉnh các nút phân trang */
.pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 5px;
      /* Giảm khoảng cách giữa các nút */
}

.pagination-btn,
.pagination-page {
      width: 30px;
      /* Nút nhỏ hơn */
      height: 30px;
      /* Nút nhỏ hơn */
      font-size: 12px;
      /* Giảm kích thước chữ trên các nút */
      border-radius: 5px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: background-color 0.2s ease;
}

/* Các nút khi hover */
.pagination-btn:hover,
.pagination-page:hover {
      background-color: #e0e0e0;
}

/* Tối ưu hóa kiểu cho input trong bảng */
input[type="number"] {
      width: 60px;
      /* Giới hạn chiều rộng của ô nhập số lượng */
      padding: 5px;
      font-size: 12px;
}

/* Giảm cỡ chữ trong bảng */
table th,
table td {
      font-size: 0.775rem;
      /* Đặt cỡ chữ nhỏ hơn */
}

/* Nếu bạn muốn giảm cỡ chữ cho những cột quan trọng khác, ví dụ như cho phần tổng tiền */
table td.total-column,
table th.total-column {
      font-size: 0.75rem;
      /* Cỡ chữ nhỏ cho cột tổng tiền */
}

tr {
      cursor: pointer;
}
</style>